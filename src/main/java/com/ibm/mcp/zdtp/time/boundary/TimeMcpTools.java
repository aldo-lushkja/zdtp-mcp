package com.ibm.mcp.zdtp.time.boundary;

import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;
import com.ibm.mcp.zdtp.time.control.TimeLogService;
import com.ibm.mcp.zdtp.time.entity.TimeEntryDto;

public class TimeMcpTools {
    private final TimeLogService logService;

    public TimeMcpTools(TimeLogService logService) {
        this.logService = logService;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private TimeLogService logService;
        public Builder logService(TimeLogService logService) { this.logService = logService; return this; }
        public TimeMcpTools build() { return new TimeMcpTools(logService); }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("time_log", "Log spent effort (hours) on a User Story, Task, or Bug.",
                schema.object()
                        .prop("entityId", schema.integer().required().withDescription("ID of the entity (Story, Task, Bug) to log time for."))
                        .prop("spentHours", schema.number().required().withDescription("Number of spent hours (e.g., 2.5)."))
                        .prop("description", schema.string().withDescription("Description of work performed."))
                        .build(),
                args -> logTime(args.path("entityId").asInt(), args.path("spentHours").asDouble(), args.path("description").asText(null)));
    }

    private String logTime(int entityId, double spentHours, String description) {
        TimeEntryDto entry = logService.logTime(entityId, spentHours, description);
        return "Logged %.2f hours on entity [%d]. Entry ID: %d."
                .formatted(entry.spent(), entityId, entry.id());
    }
}
