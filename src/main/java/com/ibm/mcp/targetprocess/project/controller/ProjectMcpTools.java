package com.ibm.mcp.targetprocess.project.controller;

import com.ibm.mcp.targetprocess.project.dto.ProjectDto;
import com.ibm.mcp.targetprocess.project.service.ProjectSearchService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMcpTools {

    private final ProjectSearchService projectSearchService;

    public ProjectMcpTools(ProjectSearchService projectSearchService) {
        this.projectSearchService = projectSearchService;
    }

    @Tool(description = """
            Search for projects in Targetprocess. \
            Supports filtering by project name and creation date range (YYYY-MM-DD). \
            Results are ordered by creation date descending.""")
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