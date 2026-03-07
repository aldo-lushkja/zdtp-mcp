package com.ibm.mcp.targetprocess.epic.controller;

import com.ibm.mcp.targetprocess.epic.dto.EpicDto;
import com.ibm.mcp.targetprocess.epic.service.EpicCreateService;
import com.ibm.mcp.targetprocess.epic.service.EpicGetByIdService;
import com.ibm.mcp.targetprocess.epic.service.EpicSearchService;
import com.ibm.mcp.targetprocess.epic.service.EpicUpdateService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EpicMcpTools {

    private final EpicSearchService epicSearchService;
    private final EpicCreateService epicCreateService;
    private final EpicUpdateService epicUpdateService;
    private final EpicGetByIdService epicGetByIdService;

    public EpicMcpTools(EpicSearchService epicSearchService,
                        EpicCreateService epicCreateService,
                        EpicUpdateService epicUpdateService,
                        EpicGetByIdService epicGetByIdService) {
        this.epicSearchService = epicSearchService;
        this.epicCreateService = epicCreateService;
        this.epicUpdateService = epicUpdateService;
        this.epicGetByIdService = epicGetByIdService;
    }

    @Tool(description = """
            Search for epics in Targetprocess. \
            Supports filtering by epic name, project name, owner login, \
            and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.""")
    public String searchEpics(String nameQuery, String projectName,
                              String ownerLogin, String startDate,
                              String endDate, int take) {
        List<EpicDto> epics = epicSearchService.searchEpics(
                nameQuery, projectName, ownerLogin, startDate, endDate, take);

        if (epics.isEmpty()) {
            return "No epics found.";
        }

        return String.join("\n", epics.stream().map(this::format).toList());
    }

    @Tool(description = """
            Create a new epic in Targetprocess. \
            Requires name and projectId (numeric ID of the project). \
            Description and effort (story points) are optional. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String createEpic(String name, int projectId, String description, Double effort) {
        EpicDto epic = epicCreateService.createEpic(name, projectId, description, effort);
        return "Created: " + format(epic);
    }

    @Tool(description = """
            Update an existing epic in Targetprocess by its numeric ID. \
            All fields except id are optional — only provided (non-blank) fields are updated. \
            stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String updateEpic(int id, String name, String description, String stateName, Double effort) {
        EpicDto epic = epicUpdateService.updateEpic(id, name, description, stateName, effort);
        return "Updated: " + format(epic);
    }

    @Tool(description = "Get an epic by its numeric ID. Returns full details including description.")
    public String getEpicById(int id) {
        EpicDto epic = epicGetByIdService.getById(id);
        return format(epic) + "\nDescription:\n" + nullSafe(epic.description());
    }

    private String format(EpicDto e) {
        return "[%d] %s (Project: %s, State: %s, Owner: %s, Points: %s, Created: %s, Done: %s)"
            .formatted(
                e.id(), e.name(),
                nullSafe(e.projectName()), nullSafe(e.state()),
                nullSafe(e.ownerLogin()),
                e.effort() != null ? e.effort().toString() : "N/A",
                nullSafe(e.createdAt()), nullSafe(e.endDate())
            );
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}