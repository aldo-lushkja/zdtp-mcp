package com.ibm.mcp.targetprocess.userstory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessClientException;
import com.ibm.mcp.targetprocess.userstory.converter.UserStoryConverter;
import com.ibm.mcp.targetprocess.userstory.dto.UserStoryDto;
import com.ibm.mcp.targetprocess.userstory.model.UserStory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserStoryCreateService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],AssignedUser[Id,Login]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final UserStoryConverter converter;
    private final ObjectMapper objectMapper;

    public UserStoryCreateService(TargetProcessProperties properties,
                                  TargetProcessHttpClient httpClient,
                                  UserStoryConverter converter,
                                  ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public UserStoryDto createUserStory(String name, int projectId, String description, Double effort) {
        String url = buildUrl();
        String body = buildBody(name, projectId, description, effort);
        String response = httpClient.post(url, body);
        UserStory story = httpClient.parseSingle(response, UserStory.class);
        return converter.toDto(story);
    }

    private String buildUrl() {
        return properties.baseUrl() + "/api/v1/UserStories"
                + "?format=json"
                + "&include=" + UriUtils.encodeQueryParam(INCLUDE, StandardCharsets.UTF_8)
                + "&access_token=" + UriUtils.encodeQueryParam(properties.accessToken(), StandardCharsets.UTF_8);
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