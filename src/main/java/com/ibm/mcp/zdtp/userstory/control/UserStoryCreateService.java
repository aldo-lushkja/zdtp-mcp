package com.ibm.mcp.zdtp.userstory.control;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.*;
import com.ibm.mcp.zdtp.userstory.entity.*;

public class UserStoryCreateService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],AssignedUser[Id,Login],Release[Id,Name]]";
    private final UserStoryConverter converter;
    private final ObjectMapper objectMapper;

    public UserStoryCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, UserStoryConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient);
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public UserStoryDto createUserStory(String name, int projectId, String description, Double effort) {
        return create(name, projectId, description, effort);
    }

    public UserStoryDto create(String name, int projectId, String description, Double effort) {
        try {
            Map<String, Object> bodyMap = new LinkedHashMap<>();
            bodyMap.put("Name", name);
            bodyMap.put("Project", Map.of("Id", projectId));
            if (description != null && !description.isBlank()) {
                bodyMap.put("Description", description);
            }
            if (effort != null) {
                bodyMap.put("Effort", effort);
            }
            
            String jsonBody = objectMapper.writeValueAsString(bodyMap);
            Map<String, String> parameters = new TreeMap<>();
            parameters.put("include", INCLUDE);
            return postSingle("UserStories", parameters, jsonBody, UserStory.class, converter::toDto);
        } catch (Exception e) {
            if (e instanceof TargetProcessApiException) throw (TargetProcessApiException) e;
            throw new TargetProcessClientException("Failed to serialize create request body", e);
        }
    }
}
