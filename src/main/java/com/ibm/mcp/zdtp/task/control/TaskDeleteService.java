package com.ibm.mcp.zdtp.task.control;

import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class TaskDeleteService extends BaseService {
    public TaskDeleteService(QueryEngine engine) {
        super(engine);
    }

    public void delete(int id) {
        engine.delete(QueryEngine.TASK, id);
    }
}
