package com.ibm.mcp.zdtp.userstory.control;

import java.net.URLEncoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.entity.TargetProcessResponse;
import com.ibm.mcp.zdtp.userstory.control.UserStoryConverter;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;
import com.ibm.mcp.zdtp.userstory.entity.UserStory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
                                                String endDate, int take, Integer releaseId,
                                                Integer teamIterationId) {
        String url = buildUrl(nameQuery, projectName, creatorLogin, startDate, endDate, take, releaseId, teamIterationId);
        String body = httpClient.fetch(url);
        TargetProcessResponse<UserStory> resp = httpClient.parse(body, new TypeReference<>() {});
        return Optional.ofNullable(resp.items()).orElse(List.of())
                .stream().map(converter::toDto).toList();
    }

    // ── URL building ────────────────────────────────────────────────────────────

    private String buildUrl(String nameQuery, String projectName,
                            String creatorLogin, String startDate, String endDate, int take,
                            Integer releaseId, Integer teamIterationId) {
        String where = buildWhere(nameQuery, projectName, creatorLogin, startDate, endDate, releaseId, teamIterationId);
        return assembleUrl(where, take);
    }

    private String buildWhere(String nameQuery, String projectName,
                              String creatorLogin, String startDate, String endDate,
                              Integer releaseId, Integer teamIterationId) {
        List<String> conditions = collectConditions(nameQuery, projectName, creatorLogin, startDate, endDate, releaseId, teamIterationId);
        return String.join(" and ", conditions);
    }

    private List<String> collectConditions(String nameQuery, String projectName,
                                           String creatorLogin, String startDate, String endDate,
                                           Integer releaseId, Integer teamIterationId) {
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
        if (releaseId != null && releaseId > 0) {
            conditions.add("Release.Id eq %d".formatted(releaseId));
        }
        if (teamIterationId != null && teamIterationId > 0) {
            conditions.add("TeamIteration.Id eq %d".formatted(teamIterationId));
        }
        return conditions;
    }

    private String assembleUrl(String where, int take) {
        String include = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],AssignedUser[Id,Login],Release[Id,Name],TeamIteration[Id,Name]]";
        return properties.baseUrl() + "/api/v1/UserStories"
                + "?where=" + URLEncoder.encode(where, StandardCharsets.UTF_8).replace("+", "%20")
                + "&include=" + URLEncoder.encode(include, StandardCharsets.UTF_8).replace("+", "%20")
                + "&orderByDesc=CreateDate"
                + "&take=" + take
                + "&format=json"
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}
