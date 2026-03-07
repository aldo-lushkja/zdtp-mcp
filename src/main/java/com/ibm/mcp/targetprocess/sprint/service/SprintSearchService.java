package com.ibm.mcp.targetprocess.sprint.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.model.TargetProcessResponse;
import com.ibm.mcp.targetprocess.sprint.converter.SprintConverter;
import com.ibm.mcp.targetprocess.sprint.dto.SprintDto;
import com.ibm.mcp.targetprocess.sprint.model.Sprint;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SprintSearchService {

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final SprintConverter converter;

    public SprintSearchService(TargetProcessProperties properties,
                               TargetProcessHttpClient httpClient,
                               SprintConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    public List<SprintDto> searchSprints(String nameQuery, String projectName,
                                         String ownerLogin, String startDate,
                                         String endDate, int take) {
        String url = buildUrl(nameQuery, projectName, ownerLogin, startDate, endDate, take);
        String body = httpClient.fetch(url);
        TargetProcessResponse<Sprint> resp = httpClient.parse(body, new TypeReference<>() {});
        return Optional.ofNullable(resp.items()).orElse(List.of())
                .stream().map(converter::toDto).toList();
    }

    // ── URL building ────────────────────────────────────────────────────────────

    private String buildUrl(String nameQuery, String projectName,
                            String ownerLogin, String startDate, String endDate, int take) {
        List<String> conditions = new ArrayList<>();
        if (nameQuery != null && !nameQuery.isBlank()) {
            conditions.add("Name contains '%s'".formatted(nameQuery));
        }
        if (projectName != null && !projectName.isBlank()) {
            conditions.add("Project.Name contains '%s'".formatted(projectName));
        }
        if (ownerLogin != null && !ownerLogin.isBlank()) {
            conditions.add("Owner.Login eq '%s'".formatted(ownerLogin));
        }
        if (startDate != null && !startDate.isBlank()) {
            conditions.add("CreateDate gte '%s'".formatted(startDate));
        }
        if (endDate != null && !endDate.isBlank()) {
            conditions.add("CreateDate lt '%s'".formatted(endDate));
        }
        String where = String.join(" and ", conditions);
        String include = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,StartDate,EndDate,Owner[Id,Login]]";
        return properties.baseUrl() + "/api/v1/Sprints"
                + "?where=" + UriUtils.encodeQueryParam(where, StandardCharsets.UTF_8)
                + "&include=" + UriUtils.encodeQueryParam(include, StandardCharsets.UTF_8)
                + "&orderByDesc=CreateDate"
                + "&take=" + take
                + "&format=json"
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}