package com.ibm.mcp.zdtp.request.control;

import java.net.URLEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.request.control.RequestConverter;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.request.entity.Request;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessClientException;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
public class RequestCreateService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final RequestConverter converter;
    private final ObjectMapper objectMapper;

    public RequestCreateService(TargetProcessProperties properties,
                                TargetProcessHttpClient httpClient,
                                RequestConverter converter,
                                ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public RequestDto createRequest(String name, int projectId, String description, Double effort) {
        String url = buildUrl();
        String body = buildBody(name, projectId, description, effort);
        String response = httpClient.post(url, body);
        Request request = httpClient.parseSingle(response, Request.class);
        return converter.toDto(request);
    }

    private String buildUrl() {
        return properties.baseUrl() + "/api/v1/Requests"
                + "?format=json"
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String buildBody(String name, int projectId, String description, Double effort) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("Name", name);
            body.put("Project", Map.of("Id", projectId));
            if (description != null && !description.isBlank()) {
                body.put("Description", description);
            }
            if (effort != null) {
                body.put("Effort", effort);
            }
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new TargetProcessClientException("Failed to serialize create request body", e);
        }
    }
}
