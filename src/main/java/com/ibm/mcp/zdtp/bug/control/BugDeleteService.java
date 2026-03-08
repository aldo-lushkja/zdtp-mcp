package com.ibm.mcp.zdtp.bug.control;

import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class BugDeleteService extends BaseService {
    public BugDeleteService(QueryEngine engine) {
        super(engine);
    }

    public void delete(int id) {
        engine.delete(QueryEngine.BUG, id);
    }
}
