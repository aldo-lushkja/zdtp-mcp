package com.ibm.mcp.zdtp.assignment.boundary;

import com.ibm.mcp.zdtp.assignment.control.AssignmentAddService;
import com.ibm.mcp.zdtp.assignment.control.AssignmentRemoveService;
import com.ibm.mcp.zdtp.assignment.entity.Assignment;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class AssignmentMcpTools {
    private final AssignmentAddService addService;
    private final AssignmentRemoveService removeService;

    public AssignmentMcpTools(AssignmentAddService addService, AssignmentRemoveService removeService) {
        this.addService = addService;
        this.removeService = removeService;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private AssignmentAddService addService;
        private AssignmentRemoveService removeService;

        public Builder addService(AssignmentAddService addService) { this.addService = addService; return this; }
        public Builder removeService(AssignmentRemoveService removeService) { this.removeService = removeService; return this; }
        public AssignmentMcpTools build() { return new AssignmentMcpTools(addService, removeService); }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("assignment_add", "Assign a user to an entity.",
                schema.object()
                        .prop("entityId", schema.integer().required().withDescription("ID of the assignable entity."))
                        .prop("userId", schema.integer().required().withDescription("ID of the user to assign."))
                        .prop("roleId", schema.integer().withDescription("Optional role ID for assignment."))
                        .build(),
                args -> {
                    Assignment a = addService.addAssignment(
                            args.path("entityId").asInt(),
                            args.path("userId").asInt(),
                            args.has("roleId") ? args.path("roleId").asInt() : null
                    );
                    return "Assignment [%d] created for user [%d] on entity [%d].".formatted(a.id(), args.path("userId").asInt(), args.path("entityId").asInt());
                });

        server.registerTool("assignment_remove", "Remove an assignment.",
                schema.object()
                        .prop("id", schema.integer().required().withDescription("Assignment ID to delete."))
                        .build(),
                args -> {
                    int id = args.path("id").asInt();
                    removeService.removeAssignment(id);
                    return "Assignment [%d] removed successfully.".formatted(id);
                });
    }
}
