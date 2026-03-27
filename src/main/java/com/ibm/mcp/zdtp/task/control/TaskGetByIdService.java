package com.ibm.mcp.zdtp.task.control;

import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.task.entity.Task;
import com.ibm.mcp.zdtp.task.entity.TaskDto;

public class TaskGetByIdService extends BaseService {
    private final TaskConverter converter;

    public TaskGetByIdService(QueryEngine engine, TaskConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public TaskDto get(int id) {
        return engine.get(QueryEngine.TASK, id, converter::toDto, Task.class);
    }
}
