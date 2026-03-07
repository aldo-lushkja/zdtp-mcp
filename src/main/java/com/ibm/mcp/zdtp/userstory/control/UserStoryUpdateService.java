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
public class UserStoryUpdateService {

    private static final String INCLUDE =
            "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],AssignedUser[Id,Login],Release[Id,Name]]";

    private final TargetProcessProperties properties;
    private final TargetProcessHttpClient httpClient;
    private final UserStoryConverter converter;
    private final ObjectMapper objectMapper;

    public UserStoryUpdateService(TargetProcessProperties properties,
                                  TargetProcessHttpClient httpClient,
                                  UserStoryConverter converter,
                                  ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public UserStoryDto updateUserStory(int id, String name, String description, String stateName, Double effort) {
        String url = buildUrl(id);
        String body = buildBody(name, description, stateName, effort);
        String response = httpClient.post(url, body);
        UserStory story = httpClient.parseSingle(response, UserStory.class);
        return converter.toDto(story);
    }

    private String buildUrl(int id) {
        return properties.baseUrl() + "/api/v1/UserStories/" + id
                + "?format=json"
                + "&include=" + URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8).replace("+", "%20")
                + "&access_token=" + URLEncoder.encode(properties.accessToken(), StandardCharsets.UTF_8).replace("+", "%20");
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