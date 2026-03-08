package com.ibm.mcp.zdtp.task.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.task.entity.Task;
import com.ibm.mcp.zdtp.task.entity.TaskDto;

public class TaskCreateService extends BaseService {
    private final TaskConverter converter;

    public TaskCreateService(QueryEngine engine, TaskConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public TaskDto create(String name, int projectId, String description, int userStoryId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Name", name);
        body.put("Project", Map.of("Id", projectId));
        body.put("UserStory", Map.of("Id", userStoryId));
        
        if (description != null && !description.isBlank()) {
            body.put("Description", convertMarkdown(description));
        }
        
        return engine.create(QueryEngine.TASK, body, converter::toDto, Task.class);
    }
}
