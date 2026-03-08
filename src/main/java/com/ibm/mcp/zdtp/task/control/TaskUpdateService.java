package com.ibm.mcp.zdtp.task.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.task.entity.Task;
import com.ibm.mcp.zdtp.task.entity.TaskDto;

public class TaskUpdateService extends BaseService {
    private final TaskConverter converter;

    public TaskUpdateService(QueryEngine engine, TaskConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public TaskDto update(int id, String name, String description, String stateName) {
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
        
        return engine.update(QueryEngine.TASK, id, body, converter::toDto, Task.class);
    }
}
