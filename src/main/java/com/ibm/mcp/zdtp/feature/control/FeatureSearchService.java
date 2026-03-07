package com.ibm.mcp.zdtp.feature.control;

import java.net.URLEncoder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.feature.control.FeatureConverter;
import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.entity.TargetProcessResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class FeatureSearchService {

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final FeatureConverter converter;

    public FeatureSearchService(TargetProcessProperties properties,
                                TargetProcessHttpClient httpClient,
                                FeatureConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    public List<FeatureDto> searchFeatures(String nameQuery, String projectName,
                                           String ownerLogin, String startDate,
                                           String endDate, int take, Integer teamIterationId) {
        String url = buildUrl(nameQuery, projectName, ownerLogin, startDate, endDate, take, teamIterationId);
        String body = httpClient.fetch(url);
        TargetProcessResponse<Feature> resp = httpClient.parse(body, new TypeReference<>() {});
        return Optional.ofNullable(resp.items()).orElse(List.of())
                .stream().map(converter::toDto).toList();
    }

    // ── URL building ────────────────────────────────────────────────────────────

    private String buildUrl(String nameQuery, String projectName,
                            String ownerLogin, String startDate, String endDate, int take, Integer teamIterationId) {
        String where = buildWhere(nameQuery, projectName, ownerLogin, startDate, endDate, teamIterationId);
        return assembleUrl(where, take);
    }

    private String buildWhere(String nameQuery, String projectName,
                              String ownerLogin, String startDate, String endDate, Integer teamIterationId) {
        List<String> conditions = collectConditions(nameQuery, projectName, ownerLogin, startDate, endDate, teamIterationId);
        return String.join(" and ", conditions);
    }

    private List<String> collectConditions(String nameQuery, String projectName,
                                           String ownerLogin, String startDate, String endDate,
                                           Integer teamIterationId) {
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
        if (teamIterationId != null) {
            conditions.add("TeamIteration.Id eq %d".formatted(teamIterationId));
        }
        return conditions;
    }

    private String assembleUrl(String where, int take) {
        String include = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],TeamIteration[Id,Name]]";
        return properties.baseUrl() + "/api/v1/Features"
                + "?where=" + URLEncoder.encode(where, StandardCharsets.UTF_8).replace("+", "%20")
                + "&include=" + URLEncoder.encode(include, StandardCharsets.UTF_8).replace("+", "%20")
                + "&orderByDesc=CreateDate"
                + "&take=" + take
                + "&format=json"
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }
}
