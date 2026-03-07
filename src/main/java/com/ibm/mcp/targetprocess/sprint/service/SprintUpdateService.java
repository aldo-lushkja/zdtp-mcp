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
public class SprintUpdateService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,StartDate,EndDate,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final SprintConverter converter;
    private final ObjectMapper objectMapper;

    public SprintUpdateService(TargetProcessProperties properties,
                               TargetProcessHttpClient httpClient,
                               SprintConverter converter,
                               ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public SprintDto updateSprint(int id, String name, String description, String stateName) {
        String url = buildUrl(id);
        String body = buildBody(name, description, stateName);
        String response = httpClient.post(url, body);
        Sprint sprint = httpClient.parseSingle(response, Sprint.class);
        return converter.toDto(sprint);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/Sprints/" + id
                + "?format=json"
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }

    private String buildBody(String name, String description, String stateName) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            if (name != null && !name.isBlank()) {
                body.put("Name", name);
            }
            if (description != null && !description.isBlank()) {
                body.put("Description", description);
            }
            if (stateName != null && !stateName.isBlank()) {
                body.put("EntityState", Map.of("Name", stateName));
            }
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to serialize sprint update request body", e);
        }
    }
}