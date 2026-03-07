package com.ibm.mcp.targetprocess.sprint.controller;

import com.ibm.mcp.targetprocess.sprint.dto.SprintDto;
import com.ibm.mcp.targetprocess.sprint.service.SprintCreateService;
import com.ibm.mcp.targetprocess.sprint.service.SprintGetByIdService;
import com.ibm.mcp.targetprocess.sprint.service.SprintSearchService;
import com.ibm.mcp.targetprocess.sprint.service.SprintUpdateService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SprintMcpTools {

    private final SprintSearchService sprintSearchService;
    private final SprintCreateService sprintCreateService;
    private final SprintUpdateService sprintUpdateService;
    private final SprintGetByIdService sprintGetByIdService;

    public SprintMcpTools(SprintSearchService sprintSearchService,
                          SprintCreateService sprintCreateService,
                          SprintUpdateService sprintUpdateService,
                          SprintGetByIdService sprintGetByIdService) {
        this.sprintSearchService = sprintSearchService;
        this.sprintCreateService = sprintCreateService;
        this.sprintUpdateService = sprintUpdateService;
        this.sprintGetByIdService = sprintGetByIdService;
    }

    @Tool(description = """
            Search for sprints in Targetprocess. \
            Supports filtering by sprint name, project name, owner login, \
            and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.""")
    public String searchSprints(String nameQuery, String projectName,
                                String ownerLogin, String startDate,
                                String endDate, int take) {
        List<SprintDto> sprints = sprintSearchService.searchSprints(
                nameQuery, projectName, ownerLogin, startDate, endDate, take);

        if (sprints.isEmpty()) {
            return "No sprints found.";
        }

        return String.join("\n", sprints.stream().map(this::format).toList());
    }

    @Tool(description = """
            Create a new sprint in Targetprocess. \
            Requires name and projectId (numeric ID of the project). \
            Description is optional. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String createSprint(String name, int projectId, String description) {
        SprintDto sprint = sprintCreateService.createSprint(name, projectId, description);
        return "Created: " + format(sprint);
    }

    @Tool(description = """
            Update an existing sprint in Targetprocess by its numeric ID. \
            All fields except id are optional — only provided (non-blank) fields are updated. \
            stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String updateSprint(int id, String name, String description, String stateName) {
        SprintDto sprint = sprintUpdateService.updateSprint(id, name, description, stateName);
        return "Updated: " + format(sprint);
    }

    @Tool(description = "Get a sprint by its numeric ID. Returns full details including description.")
    public String getSprintById(int id) {
        SprintDto sprint = sprintGetByIdService.getById(id);
        return format(sprint) + "\nDescription:\n" + nullSafe(sprint.description());
    }

    private String format(SprintDto s) {
        return "[%d] %s (Project: %s, State: %s, Owner: %s, Created: %s, Start: %s, End: %s)"
            .formatted(
                s.id(), s.name(),
                nullSafe(s.projectName()), nullSafe(s.state()),
                nullSafe(s.ownerLogin()),
                nullSafe(s.createdAt()), nullSafe(s.startDate()), nullSafe(s.endDate())
            );
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}