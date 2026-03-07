package com.ibm.mcp.targetprocess.epic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.epic.converter.EpicConverter;
import com.ibm.mcp.targetprocess.epic.dto.EpicDto;
import com.ibm.mcp.targetprocess.epic.model.Epic;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessClientException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class EpicUpdateService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final EpicConverter converter;
    private final ObjectMapper objectMapper;

    public EpicUpdateService(TargetProcessProperties properties,
                             TargetProcessHttpClient httpClient,
                             EpicConverter converter,
                             ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public EpicDto updateEpic(int id, String name, String description, String stateName, Double effort) {
        String url = buildUrl(id);
        String body = buildBody(name, description, stateName, effort);
        String response = httpClient.post(url, body);
        Epic epic = httpClient.parseSingle(response, Epic.class);
        return converter.toDto(epic);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/Epics/" + id
                + "?format=json"
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
    }

    private String buildBody(String name, String description, String stateName, Double effort) {
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
            if (effort != null) {
                body.put("Effort", effort);
            }
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to serialize update request body", e);
        }
    }
}