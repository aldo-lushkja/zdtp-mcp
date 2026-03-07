package com.ibm.mcp.zdtp.release.boundary;

import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.release.control.ReleaseCreateService;
import com.ibm.mcp.zdtp.release.control.ReleaseGetByIdService;
import com.ibm.mcp.zdtp.release.control.ReleaseSearchService;
import com.ibm.mcp.zdtp.release.control.ReleaseUpdateService;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class ReleaseMcpTools {

    private final ReleaseSearchService releaseSearchService;
    private final ReleaseCreateService releaseCreateService;
    private final ReleaseUpdateService releaseUpdateService;
    private final ReleaseGetByIdService releaseGetByIdService;

    public ReleaseMcpTools(ReleaseSearchService releaseSearchService,
                           ReleaseCreateService releaseCreateService,
                           ReleaseUpdateService releaseUpdateService,
                           ReleaseGetByIdService releaseGetByIdService) {
        this.releaseSearchService = releaseSearchService;
        this.releaseCreateService = releaseCreateService;
        this.releaseUpdateService = releaseUpdateService;
        this.releaseGetByIdService = releaseGetByIdService;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("release_search", 
            "Search for releases in Targetprocess. Supports filtering by release name, project name, owner login, start date range (YYYY-MM-DD), and teamIterationId (numeric team iteration ID). Results are ordered by creation date descending.",
            schema.object()
                .prop("nameQuery", schema.string())
                .prop("projectName", schema.string())
                .prop("ownerLogin", schema.string())
                .prop("startDate", schema.string())
                .prop("endDate", schema.string())
                .prop("take", schema.integer().withDefault(10))
                .prop("teamIterationId", schema.integer())
                .build(),
            args -> searchReleases(
                args.path("nameQuery").asText(null),
                args.path("projectName").asText(null),
                args.path("ownerLogin").asText(null),
                args.path("startDate").asText(null),
                args.path("endDate").asText(null),
                args.path("take").asInt(10),
                args.has("teamIterationId") ? args.path("teamIterationId").asInt() : null
            )
        );

        server.registerTool("release_create",
            "Create a new release in Targetprocess. Requires name and projectId (numeric ID of the project). Description and effort (story points) are optional. IMPORTANT — description formatting rules: (1) Always use HTML, never plain markdown. (2) To embed a diagram, encode the Mermaid definition in base64 and use: <img src=\"https://mermaid.ink/img/<base64>\" alt=\"diagram description\" />",
            schema.object()
                .prop("name", schema.string().required())
                .prop("projectId", schema.integer().required())
                .prop("description", schema.string())
                .prop("effort", schema.number())
                .build(),
            args -> createRelease(
                args.path("name").asText(),
                args.path("projectId").asInt(),
                args.path("description").asText(null),
                args.has("effort") ? args.path("effort").asDouble() : null
            )
        );

        server.registerTool("release_update",
            "Update an existing release in Targetprocess by its numeric ID. All fields except id are optional — only provided (non-blank) fields are updated. stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'.",
            schema.object()
                .prop("id", schema.integer().required())
                .prop("name", schema.string())
                .prop("description", schema.string())
                .prop("stateName", schema.string())
                .prop("effort", schema.number())
                .build(),
            args -> updateRelease(
                args.path("id").asInt(),
                args.path("name").asText(null),
                args.path("description").asText(null),
                args.path("stateName").asText(null),
                args.has("effort") ? args.path("effort").asDouble() : null
            )
        );

        server.registerTool("release_get",
            "Get a release by its numeric ID. Returns full details including description.",
            schema.object()
                .prop("id", schema.integer().required())
                .build(),
            args -> getReleaseById(args.path("id").asInt())
        );
    }

    public String searchReleases(String nameQuery, String projectName,
                                 String ownerLogin, String startDate,
                                 String endDate, int take, Integer teamIterationId) {
        List<ReleaseDto> releases = releaseSearchService.searchReleases(
                nameQuery, projectName, ownerLogin, startDate, endDate, take, teamIterationId);

        if (releases.isEmpty()) {
            return "No releases found.";
        }

        return String.join("\n", releases.stream().map(this::format).toList());
    }

    public String createRelease(String name, int projectId, String description, Double effort) {
        ReleaseDto release = releaseCreateService.createRelease(name, projectId, description, effort);
        return "Created: " + format(release);
    }

    public String updateRelease(int id, String name, String description, String stateName, Double effort) {
        ReleaseDto release = releaseUpdateService.updateRelease(id, name, description, stateName, effort);
        return "Updated: " + format(release);
    }

    public String getReleaseById(int id) {
        ReleaseDto release = releaseGetByIdService.getById(id);
        return format(release) + "\nDescription:\n" + nullSafe(release.description());
    }

    private String format(ReleaseDto r) {
        return "[%d] %s (Project: %s, State: %s, Owner: %s, Points: %s, Created: %s, Start: %s, End: %s)"
            .formatted(
                r.id(), r.name(),
                nullSafe(r.projectName()), nullSafe(r.state()),
                nullSafe(r.ownerLogin()),
                r.effort() != null ? r.effort().toString() : "N/A",
                nullSafe(r.createdAt()), nullSafe(r.startDate()), nullSafe(r.endDate())
            );
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}
