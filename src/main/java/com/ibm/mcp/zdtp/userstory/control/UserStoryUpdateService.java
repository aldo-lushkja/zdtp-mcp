package com.ibm.mcp.zdtp.userstory.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.userstory.entity.UserStory;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;

public class UserStoryUpdateService extends BaseService {
    private final UserStoryConverter converter;

    public UserStoryUpdateService(QueryEngine engine, UserStoryConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public UserStoryDto updateUserStory(int id, String name, String description, String stateName, Double effort) {
        return update(id, name, description, stateName, effort, null, null, null, null);
    }

    public UserStoryDto update(int id, String name, String description, String stateName, Double effort, Integer featureId, Integer teamIterationId, Integer teamId, Integer releaseId) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) {
            body.put("Name", name);
        }
        if (description != null) {
            body.put("Description", convertMarkdown(description));
        }
        if (stateName != null && !stateName.isBlank()) {
            body.put("EntityState", Map.of("Name", stateName));
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

        return engine.update(QueryEngine.USER_STORY, id, body, converter::toDto, UserStory.class);
    }
}

