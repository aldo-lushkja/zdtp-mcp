package com.ibm.mcp.zdtp.teamiteration.boundary;

import com.ibm.mcp.zdtp.teamiteration.entity.TeamIterationDto;
import com.ibm.mcp.zdtp.teamiteration.control.TeamIterationGetByIdService;
import com.ibm.mcp.zdtp.teamiteration.control.TeamIterationSearchService;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class TeamIterationMcpTools {

    private final TeamIterationSearchService searchService;
    private final TeamIterationGetByIdService getByIdService;

    public TeamIterationMcpTools(TeamIterationSearchService searchService,
                                  TeamIterationGetByIdService getByIdService) {
        this.searchService = searchService;
        this.getByIdService = getByIdService;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("team_iteration_search", 
            "Search for team iterations (sprints) in Targetprocess. Supports filtering by name, teamId (numeric team ID), teamName, and date range (startDate/endDate in YYYY-MM-DD format, filters on StartDate). Results are ordered by start date descending.",
            schema.object()
                .prop("nameQuery", schema.string())
                .prop("teamId", schema.integer())
                .prop("teamName", schema.string())
                .prop("startDate", schema.string())
                .prop("endDate", schema.string())
                .prop("take", schema.integer().withDefault(10))
                .build(),
            args -> searchTeamIterations(
                args.path("nameQuery").asText(null),
                args.has("teamId") ? args.path("teamId").asInt() : null,
                args.path("teamName").asText(null),
                args.path("startDate").asText(null),
                args.path("endDate").asText(null),
                args.path("take").asInt(10)
            )
        );

        server.registerTool("team_iteration_get",
            "Get a team iteration by its numeric ID. Returns full details.",
            schema.object()
                .prop("id", schema.integer().required())
                .build(),
            args -> getTeamIterationById(args.path("id").asInt())
        );
    }

    public String searchTeamIterations(String nameQuery, Integer teamId, String teamName,
                                        String startDate, String endDate, int take) {
        List<TeamIterationDto> iterations = searchService.searchTeamIterations(
                nameQuery, teamId, teamName, startDate, endDate, take);
        if (iterations.isEmpty()) {
            return "No team iterations found.";
        }
        return String.join("\n", iterations.stream().map(this::format).toList());
    }

    public String getTeamIterationById(int id) {
        TeamIterationDto iteration = getByIdService.getById(id);
        return format(iteration);
    }

    private String format(TeamIterationDto t) {
        return "[%d] %s (Team: %s [%s], Start: %s, End: %s)".formatted(
                t.id(), t.name(),
                nullSafe(t.teamName()), t.teamId() != null ? t.teamId() : "N/A",
                nullSafe(t.startDate()), nullSafe(t.endDate()));
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}