package com.ibm.mcp.zdtp.bug.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.ibm.mcp.zdtp.bug.entity.Bug;
import com.ibm.mcp.zdtp.bug.entity.BugDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class BugUpdateService extends BaseService {
    private final BugConverter converter;

    public BugUpdateService(QueryEngine engine, BugConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public BugDto update(int id, String name, String description, String stateName, Double effort, Integer userStoryId, Integer featureId) {
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
        if (userStoryId != null && userStoryId > 0) {
            body.put("UserStory", Map.of("Id", userStoryId));
        }
        if (featureId != null && featureId > 0) {
            body.put("Feature", Map.of("Id", featureId));
        }

        return engine.update(QueryEngine.BUG, id, body, converter::toDto, Bug.class);
    }
}
