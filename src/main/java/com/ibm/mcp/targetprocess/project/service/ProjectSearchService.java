package com.ibm.mcp.targetprocess.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.project.converter.ProjectConverter;
import com.ibm.mcp.targetprocess.project.dto.ProjectDto;
import com.ibm.mcp.targetprocess.project.model.ProjectData;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.model.TargetProcessResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectSearchService {

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final ProjectConverter converter;

    public ProjectSearchService(TargetProcessProperties properties,
                                TargetProcessHttpClient httpClient,
                                ProjectConverter converter) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
    }

    // ── Public API ─────────────────────────────────────────────────────────────

    public List<ProjectDto> searchProjects(String nameQuery, String startDate,
                                           String endDate, int take) {
        String url = buildUrl(nameQuery, startDate, endDate, take);
        String body = httpClient.fetch(url);
        TargetProcessResponse<ProjectData> resp = httpClient.parse(body, new TypeReference<>() {});
        return Optional.ofNullable(resp.items()).orElse(List.of())
                .stream().map(converter::toDto).toList();
    }

    // ── URL building ────────────────────────────────────────────────────────────

    private String buildUrl(String nameQuery, String startDate, String endDate, int take) {
        String where = buildWhere(nameQuery, startDate, endDate);
        return assembleUrl(where, take);
    }

    private String buildWhere(String nameQuery, String startDate, String endDate) {
        List<String> conditions = collectConditions(nameQuery, startDate, endDate);
        return String.join(" and ", conditions);
    }

    private List<String> collectConditions(String nameQuery, String startDate, String endDate) {
        List<String> conditions = new ArrayList<>();
        if (nameQuery != null && !nameQuery.isBlank()) {
            conditions.add("Name contains '%s'".formatted(nameQuery));
        }
        if (startDate != null && !startDate.isBlank()) {
            conditions.add("CreateDate gte '%s'".formatted(startDate));
        }
        if (endDate != null && !endDate.isBlank()) {
            conditions.add("CreateDate lt '%s'".formatted(endDate));
        }
        return conditions;
    }

    private String assembleUrl(String where, int take) {
        String include = "[Id,Name,Description,EntityState[Id,Name],CreateDate]";
        return properties.baseUrl() + "/api/v1/Projects"
                + "?where=" + UriUtils.encodeQueryParam(where, StandardCharsets.UTF_8)
                + "&include=" + UriUtils.encodeQueryParam(include, StandardCharsets.UTF_8)
                + "&orderByDesc=CreateDate"
                + "&take=" + take
                + "&format=json"
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }
}