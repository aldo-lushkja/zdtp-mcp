package com.ibm.mcp.targetprocess.sprint.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessClientException;
import com.ibm.mcp.targetprocess.sprint.converter.SprintConverter;
import com.ibm.mcp.targetprocess.sprint.dto.SprintDto;
import com.ibm.mcp.targetprocess.sprint.model.Sprint;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SprintCreateService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,StartDate,EndDate,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final SprintConverter converter;
    private final ObjectMapper objectMapper;

    public SprintCreateService(TargetProcessProperties properties,
                               TargetProcessHttpClient httpClient,
                               SprintConverter converter,
                               ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public SprintDto createSprint(String name, int projectId, String description) {
        String url = buildUrl();
        String body = buildBody(name, projectId, description);
        String response = httpClient.post(url, body);
        Sprint sprint = httpClient.parseSingle(response, Sprint.class);
        return converter.toDto(sprint);
    }

    private String buildUrl() {
        return properties.baseUrl() + "/api/v1/Sprints"
                + "?format=json"
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }

    private String buildBody(String name, int projectId, String description) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Name", name);
            body.put("Project", Map.of("Id", projectId));
            if (description != null && !description.isBlank()) {
                body.put("Description", description);
            }
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to serialize sprint create request body", e);
        }
    }
}