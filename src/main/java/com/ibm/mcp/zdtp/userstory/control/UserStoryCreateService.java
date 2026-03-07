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

public class UserStoryCreateService extends BaseService {
    private final UserStoryConverter converter;

    public UserStoryCreateService(TargetProcessProperties props, TargetProcessHttpClient http, UserStoryConverter conv, ObjectMapper mapper) {
        super(props, http, mapper); this.converter = conv;
    }

    public UserStoryDto createUserStory(String name, int projectId, String description, Double effort) {
        return create(name, projectId, description, effort);
    }

    public UserStoryDto create(String name, int projectId, String description, Double effort) {
        Map<String, Object> body = new LinkedHashMap<>(); body.put("Name", name); body.put("Project", Map.of("Id", projectId));
        if (description != null && !description.isBlank()) body.put("Description", description);
        if (effort != null) body.put("Effort", effort);
        return engine.create(QueryEngine.USER_STORY, body, converter::toDto, UserStory.class);
    }
}
