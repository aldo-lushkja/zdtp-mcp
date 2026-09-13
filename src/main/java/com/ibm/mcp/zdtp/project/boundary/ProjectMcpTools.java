package com.ibm.mcp.zdtp.project.boundary;

import com.ibm.mcp.zdtp.project.entity.ProjectDto;
import com.ibm.mcp.zdtp.project.control.ProjectSearchService;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class ProjectMcpTools {
    private final ProjectSearchService searchSvc;

    public ProjectMcpTools(ProjectSearchService s) { this.searchSvc = s; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ProjectSearchService searchSvc;
        public Builder searchSvc(ProjectSearchService s) { this.searchSvc = s; return this; }
        public ProjectMcpTools build() { return new ProjectMcpTools(searchSvc); }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("project_search", "Search for projects.",
                schema.object().prop("nameQuery", schema.string()).prop("startDate", schema.string()).prop("endDate", schema.string()).prop("take", schema.integer().withDefault(10)).build(),
                args -> search(ProjectSearchService.SearchCriteria.builder()
                        .nameQuery(args.path("nameQuery").asText(null))
                        .startDate(args.path("startDate").asText(null))
                        .endDate(args.path("endDate").asText(null))
                        .take(args.path("take").asInt(10))
                        .build()));
    }

    private String search(ProjectSearchService.SearchCriteria c) {
        var res = searchSvc.search(c);
        return res.isEmpty() ? "No projects found." : String.join("\n", res.stream().map(p -> "[%d] %s".formatted(p.id(), p.name())).toList());
    }
}

