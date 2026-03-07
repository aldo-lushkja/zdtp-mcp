package com.ibm.mcp.targetprocess.userstory.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.model.TargetProcessResponse;
import com.ibm.mcp.targetprocess.userstory.converter.UserStoryConverter;
import com.ibm.mcp.targetprocess.userstory.dto.UserStoryDto;
import com.ibm.mcp.targetprocess.userstory.model.UserStory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserStorySearchService {

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final UserStoryConverter converter;

    public UserStorySearchService(TargetProcessProperties properties,
                                  TargetProcessHttpClient httpClient,
                                  UserStoryConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    public List<UserStoryDto> searchUserStories(String nameQuery, String projectName,
                                                String creatorLogin, String startDate,
                                                String endDate, int take, Integer releaseId) {
        String url = buildUrl(nameQuery, projectName, creatorLogin, startDate, endDate, take, releaseId);
        String body = httpClient.fetch(url);
        TargetProcessResponse<UserStory> resp = httpClient.parse(body, new TypeReference<>() {});
        return Optional.ofNullable(resp.items()).orElse(List.of())
                .stream().map(converter::toDto).toList();
    }

    // ── URL building ────────────────────────────────────────────────────────────

    private String buildUrl(String nameQuery, String projectName,
                            String creatorLogin, String startDate, String endDate, int take, Integer releaseId) {
        String where = buildWhere(nameQuery, projectName, creatorLogin, startDate, endDate, releaseId);
        return assembleUrl(where, take);
    }

    private String buildWhere(String nameQuery, String projectName,
                              String creatorLogin, String startDate, String endDate, Integer releaseId) {
        List<String> conditions = collectConditions(nameQuery, projectName, creatorLogin, startDate, endDate, releaseId);
        return String.join(" and ", conditions);
    }

    private List<String> collectConditions(String nameQuery, String projectName,
                                           String creatorLogin, String startDate, String endDate, Integer releaseId) {
        List<String> conditions = new ArrayList<>();
        if (nameQuery != null && !nameQuery.isBlank()) {
            conditions.add("Name contains '%s'".formatted(nameQuery));
        }
        if (projectName != null && !projectName.isBlank()) {
            conditions.add("Project.Name contains '%s'".formatted(projectName));
        }
        if (creatorLogin != null && !creatorLogin.isBlank()) {
            conditions.add("Owner.Login eq '%s'".formatted(creatorLogin));
        }
        if (startDate != null && !startDate.isBlank()) {
            conditions.add("CreateDate gte '%s'".formatted(startDate));
        }
        if (endDate != null && !endDate.isBlank()) {
            conditions.add("CreateDate lt '%s'".formatted(endDate));
        }
        if (releaseId != null) {
            conditions.add("Release.Id eq %d".formatted(releaseId));
        }
        return conditions;
    }

    private String assembleUrl(String where, int take) {
        String include = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],AssignedUser[Id,Login],Release[Id,Name]]";
        return properties.baseUrl() + "/api/v1/UserStories"
                + "?where=" + UriUtils.encodeQueryParam(where, StandardCharsets.UTF_8)
                + "&include=" + UriUtils.encodeQueryParam(include, StandardCharsets.UTF_8)
                + "&orderByDesc=CreateDate"
                + "&take=" + take
                + "&format=json"
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}
