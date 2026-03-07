package com.ibm.mcp.zdtp.epic.boundary;

import com.ibm.mcp.zdtp.epic.entity.EpicDto;
import com.ibm.mcp.zdtp.epic.control.EpicCreateService;
import com.ibm.mcp.zdtp.epic.control.EpicGetByIdService;
import com.ibm.mcp.zdtp.epic.control.EpicSearchService;
import com.ibm.mcp.zdtp.epic.control.EpicUpdateService;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

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

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("epic_search", 
            "Search for epics in Targetprocess. Supports filtering by epic name, project name, owner login, and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.",
            schema.object()
                .prop("nameQuery", schema.string())
                .prop("projectName", schema.string())
                .prop("ownerLogin", schema.string())
                .prop("startDate", schema.string())
                .prop("endDate", schema.string())
                .prop("take", schema.integer().withDefault(10))
                .build(),
            args -> searchEpics(
                args.path("nameQuery").asText(null),
                args.path("projectName").asText(null),
                args.path("ownerLogin").asText(null),
                args.path("startDate").asText(null),
                args.path("endDate").asText(null),
                args.path("take").asInt(10)
            )
        );

        server.registerTool("epic_create",
            "Create a new epic in Targetprocess. Requires name and projectId (numeric ID of the project). Description and effort (story points) are optional. IMPORTANT — description formatting rules: (1) Always use HTML, never plain markdown. (2) To embed a diagram, encode the Mermaid definition in base64 and use: <img src=\"https://mermaid.ink/img/<base64>\" alt=\"diagram description\" />",
            schema.object()
                .prop("name", schema.string().required())
                .prop("projectId", schema.integer().required())
                .prop("description", schema.string())
                .prop("effort", schema.number())
                .build(),
            args -> createEpic(
                args.path("name").asText(),
                args.path("projectId").asInt(),
                args.path("description").asText(null),
                args.has("effort") ? args.path("effort").asDouble() : null
            )
        );

        server.registerTool("epic_update",
            "Update an existing epic in Targetprocess by its numeric ID. All fields except id are optional — only provided (non-blank) fields are updated. stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'.",
            schema.object()
                .prop("id", schema.integer().required())
                .prop("name", schema.string())
                .prop("description", schema.string())
                .prop("stateName", schema.string())
                .prop("effort", schema.number())
                .build(),
            args -> updateEpic(
                args.path("id").asInt(),
                args.path("name").asText(null),
                args.path("description").asText(null),
                args.path("stateName").asText(null),
                args.has("effort") ? args.path("effort").asDouble() : null
            )
        );

        server.registerTool("epic_get",
            "Get an epic by its numeric ID. Returns full details including description.",
            schema.object()
                .prop("id", schema.integer().required())
                .build(),
            args -> getEpicById(args.path("id").asInt())
        );
    }

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

    public String createEpic(String name, int projectId, String description, Double effort) {
        EpicDto epic = epicCreateService.createEpic(name, projectId, description, effort);
        return "Created: " + format(epic);
    }

    public String updateEpic(int id, String name, String description, String stateName, Double effort) {
        EpicDto epic = epicUpdateService.updateEpic(id, name, description, stateName, effort);
        return "Updated: " + format(epic);
    }

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