package com.ibm.mcp.zdtp.bug.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.ibm.mcp.zdtp.bug.entity.Bug;
import com.ibm.mcp.zdtp.bug.entity.BugDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class BugCreateService extends BaseService {
    private final BugConverter converter;

    public BugCreateService(QueryEngine engine, BugConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public BugDto create(String name, int projectId, String description, Double effort, Integer userStoryId, Integer featureId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Name", name);
        body.put("Project", Map.of("Id", projectId));
        
        if (description != null && !description.isBlank()) {
            body.put("Description", convertMarkdown(description));
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
        
        return engine.create(QueryEngine.BUG, body, converter::toDto, Bug.class);
    }
}
