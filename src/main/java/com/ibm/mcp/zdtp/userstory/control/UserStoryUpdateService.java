package com.ibm.mcp.zdtp.userstory.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.userstory.entity.UserStory;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;

public class UserStoryUpdateService extends BaseService {
    private final UserStoryConverter converter;

    public UserStoryUpdateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, UserStoryConverter converter, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
        this.converter = converter;
    }

    public UserStoryDto updateUserStory(int id, String name, String description, String stateName, Double effort) {
        return update(id, name, description, stateName, effort);
    }

    public UserStoryDto update(int id, String name, String description, String stateName, Double effort) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) {
            body.put("Name", name);
        }
        if (description != null) {
            body.put("Description", description);
        }
        if (stateName != null && !stateName.isBlank()) {
            body.put("EntityState", Map.of("Name", stateName));
        }
        if (effort != null) {
            body.put("Effort", effort);
        }
        
        return engine.update(QueryEngine.USER_STORY, id, body, converter::toDto, UserStory.class);
    }
}
