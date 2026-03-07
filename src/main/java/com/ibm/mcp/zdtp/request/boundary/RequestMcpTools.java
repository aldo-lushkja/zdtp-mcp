package com.ibm.mcp.zdtp.request.boundary;

import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.request.control.RequestCreateService;
import com.ibm.mcp.zdtp.request.control.RequestGetByIdService;
import com.ibm.mcp.zdtp.request.control.RequestSearchService;
import com.ibm.mcp.zdtp.request.control.RequestUpdateService;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class RequestMcpTools {

    private final RequestSearchService requestSearchService;
    private final RequestCreateService requestCreateService;
    private final RequestUpdateService requestUpdateService;
    private final RequestGetByIdService requestGetByIdService;

    public RequestMcpTools(RequestSearchService requestSearchService,
                           RequestCreateService requestCreateService,
                           RequestUpdateService requestUpdateService,
                           RequestGetByIdService requestGetByIdService) {
        this.requestSearchService = requestSearchService;
        this.requestCreateService = requestCreateService;
        this.requestUpdateService = requestUpdateService;
        this.requestGetByIdService = requestGetByIdService;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("request_search", 
            "Search for requests in Targetprocess. Supports filtering by request name, project name, owner login, and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.",
            schema.object()
                .prop("nameQuery", schema.string())
                .prop("projectName", schema.string())
                .prop("ownerLogin", schema.string())
                .prop("startDate", schema.string())
                .prop("endDate", schema.string())
                .prop("take", schema.integer().withDefault(10))
                .build(),
            args -> searchRequests(
                args.path("nameQuery").asText(null),
                args.path("projectName").asText(null),
                args.path("ownerLogin").asText(null),
                args.path("startDate").asText(null),
                args.path("endDate").asText(null),
                args.path("take").asInt(10)
            )
        );

        server.registerTool("request_create",
            "Create a new request in Targetprocess. Requires name and projectId (numeric ID of the project). Description and effort (story points) are optional. IMPORTANT — description formatting rules: (1) Always use HTML, never plain markdown. (2) To embed a diagram, encode the Mermaid definition in base64 and use: <img src=\"https://mermaid.ink/img/<base64>\" alt=\"diagram description\" />",
            schema.object()
                .prop("name", schema.string().required())
                .prop("projectId", schema.integer().required())
                .prop("description", schema.string())
                .prop("effort", schema.number())
                .build(),
            args -> createRequest(
                args.path("name").asText(),
                args.path("projectId").asInt(),
                args.path("description").asText(null),
                args.has("effort") ? args.path("effort").asDouble() : null
            )
        );

        server.registerTool("request_update",
            "Update an existing request in Targetprocess by its numeric ID. All fields except id are optional — only provided (non-blank) fields are updated. stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'.",
            schema.object()
                .prop("id", schema.integer().required())
                .prop("name", schema.string())
                .prop("description", schema.string())
                .prop("stateName", schema.string())
                .prop("effort", schema.number())
                .build(),
            args -> updateRequest(
                args.path("id").asInt(),
                args.path("name").asText(null),
                args.path("description").asText(null),
                args.path("stateName").asText(null),
                args.has("effort") ? args.path("effort").asDouble() : null
            )
        );

        server.registerTool("request_get",
            "Get a request by its numeric ID. Returns full details including description.",
            schema.object()
                .prop("id", schema.integer().required())
                .build(),
            args -> getRequestById(args.path("id").asInt())
        );
    }

    public String searchRequests(String nameQuery, String projectName,
                                 String ownerLogin, String startDate,
                                 String endDate, int take) {
        List<RequestDto> requests = requestSearchService.searchRequests(
                nameQuery, projectName, ownerLogin, startDate, endDate, take);

        if (requests.isEmpty()) {
            return "No requests found.";
        }

        return String.join("\n", requests.stream().map(this::format).toList());
    }

    public String createRequest(String name, int projectId, String description, Double effort) {
        RequestDto request = requestCreateService.createRequest(name, projectId, description, effort);
        return "Created: " + format(request);
    }

    public String updateRequest(int id, String name, String description, String stateName, Double effort) {
        RequestDto request = requestUpdateService.updateRequest(id, name, description, stateName, effort);
        return "Updated: " + format(request);
    }

    public String getRequestById(int id) {
        RequestDto request = requestGetByIdService.getById(id);
        return format(request) + "\nDescription:\n" + nullSafe(request.description());
    }

    private String format(RequestDto r) {
        return "[%d] %s (Project: %s, State: %s, Owner: %s, Points: %s, Created: %s, Done: %s)"
            .formatted(
                r.id(), r.name(),
                nullSafe(r.projectName()), nullSafe(r.state()),
                nullSafe(r.ownerLogin()),
                r.effort() != null ? r.effort().toString() : "N/A",
                nullSafe(r.createdAt()), nullSafe(r.endDate())
            );
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}
