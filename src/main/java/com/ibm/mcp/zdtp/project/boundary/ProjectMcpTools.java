package com.ibm.mcp.zdtp.project.boundary;

import com.ibm.mcp.zdtp.project.entity.ProjectDto;
import com.ibm.mcp.zdtp.project.control.ProjectSearchService;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class ProjectMcpTools {

    private final ProjectSearchService projectSearchService;

    public ProjectMcpTools(ProjectSearchService projectSearchService) {
        this.projectSearchService = projectSearchService;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("project_search", 
            "Search for projects in Targetprocess. Supports filtering by project name and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.",
            schema.object()
                .prop("nameQuery", schema.string())
                .prop("startDate", schema.string())
                .prop("endDate", schema.string())
                .prop("take", schema.integer().withDefault(10))
                .build(),
            args -> searchProjects(
                args.path("nameQuery").asText(null),
                args.path("startDate").asText(null),
                args.path("endDate").asText(null),
                args.path("take").asInt(10)
            )
        );
    }

    public String searchProjects(String nameQuery, String startDate, String endDate, int take) {
        List<ProjectDto> projects = projectSearchService.searchProjects(nameQuery, startDate, endDate, take);

        if (projects.isEmpty()) {
            return "No projects found.";
        }

        return String.join("\n", projects.stream().map(this::format).toList());
    }

    private String format(ProjectDto p) {
        return "[%d] %s (State: %s, Created: %s)"
            .formatted(p.id(), p.name(), nullSafe(p.state()), nullSafe(p.createdAt()));
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}