package com.ibm.mcp.zdtp.userstory.control;

import java.net.URLEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessClientException;
import com.ibm.mcp.zdtp.userstory.control.UserStoryConverter;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;
import com.ibm.mcp.zdtp.userstory.entity.UserStory;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
public class UserStoryCreateService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],AssignedUser[Id,Login],Release[Id,Name]]";

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