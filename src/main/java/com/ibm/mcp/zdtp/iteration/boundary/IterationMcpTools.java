package com.ibm.mcp.zdtp.iteration.boundary;

import com.ibm.mcp.zdtp.iteration.control.IterationGetService;
import com.ibm.mcp.zdtp.iteration.control.IterationSearchService;
import com.ibm.mcp.zdtp.iteration.entity.Iteration;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class IterationMcpTools {
    private final IterationSearchService searchService;
    private final IterationGetService getService;

    public IterationMcpTools(IterationSearchService searchService, IterationGetService getService) {
        this.searchService = searchService;
        this.getService = getService;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private IterationSearchService searchService;
        private IterationGetService getService;

        public Builder searchService(IterationSearchService searchService) { this.searchService = searchService; return this; }
        public Builder getService(IterationGetService getService) { this.getService = getService; return this; }
        public IterationMcpTools build() { return new IterationMcpTools(searchService, getService); }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("iteration_search", "Search for project iterations.",
                schema.object()
                        .prop("nameQuery", schema.string().withDescription("Filter project iterations by name."))
                        .prop("projectId", schema.integer().withDescription("Filter by project ID."))
                        .prop("startDate", schema.string().withDescription("Filter by start date (YYYY-MM-DD)."))
                        .prop("endDate", schema.string().withDescription("Filter by end date (YYYY-MM-DD)."))
                        .prop("take", schema.integer().withDescription("Max results (default: 10)."))
                        .build(),
                args -> {
                    List<Iteration> list = searchService.search(
                            args.path("nameQuery").asText(null),
                            args.has("projectId") ? args.path("projectId").asInt() : null,
                            args.path("startDate").asText(null),
                            args.path("endDate").asText(null),
                            args.has("take") ? args.path("take").asInt() : 10
                    );
                    if (list.isEmpty()) return "No iterations found.";
                    return String.join("\n", list.stream().map(it -> "[%d] %s (Project: %s)".formatted(it.id(), it.name(), it.projectName() != null ? it.projectName() : "N/A")).toList());
                });

        server.registerTool("iteration_get", "Get project iteration details.",
                schema.object()
                        .prop("id", schema.integer().required().withDescription("Iteration ID."))
                        .build(),
                args -> {
                    Iteration it = getService.get(args.path("id").asInt());
                    return "[%d] %s (Project: %s, Dates: %s to %s)"
                            .formatted(it.id(), it.name(), it.projectName() != null ? it.projectName() : "N/A", it.startDate(), it.endDate());
                });
    }
}
