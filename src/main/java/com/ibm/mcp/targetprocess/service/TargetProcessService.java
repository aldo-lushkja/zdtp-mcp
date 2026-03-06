package com.ibm.mcp.targetprocess.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.converter.UserStoryConverter;
import com.ibm.mcp.targetprocess.dto.UserStoryDto;
import com.ibm.mcp.targetprocess.exception.TargetProcessApiException;
import com.ibm.mcp.targetprocess.exception.TargetProcessClientException;
import com.ibm.mcp.targetprocess.model.TargetProcessResponse;
import com.ibm.mcp.targetprocess.model.UserStory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TargetProcessService {

    private static final Logger log = LoggerFactory.getLogger(TargetProcessService.class);

    private final TargetProcessProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final UserStoryConverter converter;

    public TargetProcessService(TargetProcessProperties properties, ObjectMapper objectMapper,
                                HttpClient httpClient, UserStoryConverter converter) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    public List<UserStoryDto> searchUserStories(String nameQuery, String projectName,
                                                String creatorLogin, String startDate,
                                                String endDate, int take) {
        String url = buildUrl(nameQuery, projectName, creatorLogin, startDate, endDate, take);
        String body = fetch(url);
        return parseAndConvert(body);
    }

    // ── HTTP execution ──────────────────────────────────────────────────────────

    private String fetch(String url) {
        HttpResponse<String> response = send(buildRequest(url));
        validateResponse(response);
        return response.body();
    }

    private HttpRequest buildRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();
    }

    private HttpResponse<String> send(HttpRequest request) {
        log.debug("GET {}", request.uri());
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.debug("Response status={} body={}", response.statusCode(), response.body());
            return response;
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to call Targetprocess API", e);
        }
    }

    private void validateResponse(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            throw new TargetProcessApiException(response.statusCode(), response.body());
        }
    }

    // ── Parsing ─────────────────────────────────────────────────────────────────

    private List<UserStoryDto> parseAndConvert(String body) {
        return parseItems(body).stream().map(converter::toDto).toList();
    }

    private List<UserStory> parseItems(String body) {
        return Optional.ofNullable(readResponse(body).items()).orElse(List.of());
    }

    private TargetProcessResponse<UserStory> readResponse(String body) {
        try {
            return objectMapper.readValue(body, new TypeReference<>() {});
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to parse Targetprocess response", e);
        }
    }

    // ── URL building ────────────────────────────────────────────────────────────

    private String buildUrl(String nameQuery, String projectName,
                            String creatorLogin, String startDate, String endDate, int take) {
        String where = buildWhere(nameQuery, projectName, creatorLogin, startDate, endDate);
        return assembleUrl(where, take);
    }

    private String buildWhere(String nameQuery, String projectName,
                              String creatorLogin, String startDate, String endDate) {
        List<String> conditions = collectConditions(nameQuery, projectName, creatorLogin, startDate, endDate);
        return conditions.isEmpty() ? "" : "(" + String.join(" and ", conditions) + ")";
    }

    private List<String> collectConditions(String nameQuery, String projectName,
                                           String creatorLogin, String startDate, String endDate) {
        List<String> conditions = new ArrayList<>();
        if (nameQuery != null && !nameQuery.isBlank()) {
            conditions.add("Name.Contains(\"%s\")".formatted(nameQuery));
        }
        if (projectName != null && !projectName.isBlank()) {
            conditions.add("Project.Name.Contains(\"%s\")".formatted(projectName));
        }
        if (creatorLogin != null && !creatorLogin.isBlank()) {
            conditions.add("Owner.Login == \"%s\"".formatted(creatorLogin));
        }
        if (startDate != null && !startDate.isBlank()) {
            conditions.add(formatDateCondition("CreateDate >=", startDate));
        }
        if (endDate != null && !endDate.isBlank()) {
            conditions.add(formatDateCondition("CreateDate <=", endDate));
        }
        return conditions;
    }

    private String formatDateCondition(String operator, String date) {
        return "%s '%s'".formatted(operator, date);
    }

    private String assembleUrl(String where, int take) {
        String select = "{Id,Name,Description,Project:{Id,Name},EntityState:{Id,Name},CreateDate,Owner:{Id,Login}}";
        return properties.baseUrl() + "/api/v2/UserStory"
                + "?where=" + UriUtils.encodeQueryParam(where, StandardCharsets.UTF_8)
                + "&select=" + UriUtils.encodeQueryParam(select, StandardCharsets.UTF_8)
                + "&orderBy=CreateDate+desc"
                + "&take=" + take
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}
