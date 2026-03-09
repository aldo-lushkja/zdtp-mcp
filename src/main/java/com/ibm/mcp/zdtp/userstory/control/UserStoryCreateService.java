package com.ibm.mcp.zdtp.userstory.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.userstory.entity.UserStory;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;

public class UserStoryCreateService extends BaseService {
    private final UserStoryConverter converter;

    public UserStoryCreateService(QueryEngine engine, UserStoryConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public UserStoryDto createUserStory(String name, int projectId, String description, Double effort) {
        return create(name, projectId, description, effort, null, null, null, null);
    }

    public UserStoryDto create(String name, int projectId, String description, Double effort, Integer featureId, Integer teamIterationId, Integer teamId, Integer releaseId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Name", name);
        body.put("Project", Map.of("Id", projectId));

        if (description != null && !description.isBlank()) {
            body.put("Description", convertMarkdown(description));
        }

        if (effort != null) {
            body.put("Effort", effort);
        }

        if (featureId != null && featureId > 0) {
            body.put("Feature", Map.of("Id", featureId));
        }

        if (teamIterationId != null && teamIterationId > 0) {
            body.put("TeamIteration", Map.of("Id", teamIterationId));
        }

        if (teamId != null && teamId > 0) {
            body.put("Team", Map.of("Id", teamId));
        }

        if (releaseId != null && releaseId > 0) {
            body.put("Release", Map.of("Id", releaseId));
        }

        return engine.create(QueryEngine.USER_STORY, body, converter::toDto, UserStory.class);
    }
}

