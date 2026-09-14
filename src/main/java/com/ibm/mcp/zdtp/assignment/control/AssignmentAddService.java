package com.ibm.mcp.zdtp.assignment.control;

import com.ibm.mcp.zdtp.assignment.entity.Assignment;
import com.ibm.mcp.zdtp.assignment.entity.AssignmentDto;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import java.util.HashMap;
import java.util.Map;

public class AssignmentAddService {
    private final QueryEngine engine;

    public AssignmentAddService(QueryEngine engine) {
        this.engine = engine;
    }

    public Assignment addAssignment(int entityId, int userId, Integer roleId) {
        Map<String, Object> body = new HashMap<>();
        body.put("General", Map.of("Id", entityId));
        body.put("GeneralUser", Map.of("Id", userId));
        if (roleId != null) {
            body.put("Role", Map.of("Id", roleId));
        }

        return engine.create(
                QueryEngine.ASSIGNMENT,
                body,
                AssignmentConverter::toAssignment,
                AssignmentDto.class
        );
    }
}
