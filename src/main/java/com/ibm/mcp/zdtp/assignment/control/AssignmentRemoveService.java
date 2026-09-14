package com.ibm.mcp.zdtp.assignment.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class AssignmentRemoveService {
    private final QueryEngine engine;

    public AssignmentRemoveService(QueryEngine engine) {
        this.engine = engine;
    }

    public void removeAssignment(int id) {
        engine.delete(QueryEngine.ASSIGNMENT, id);
    }
}
