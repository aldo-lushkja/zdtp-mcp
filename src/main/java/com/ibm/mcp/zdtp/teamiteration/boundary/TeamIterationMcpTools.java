package com.ibm.mcp.zdtp.teamiteration.boundary;

import com.ibm.mcp.zdtp.teamiteration.control.TeamIterationGetByIdService;
import com.ibm.mcp.zdtp.teamiteration.control.TeamIterationSearchService;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class TeamIterationMcpTools {
    private final TeamIterationSearchService searchService;
    private final TeamIterationGetByIdService getService;

    public TeamIterationMcpTools(TeamIterationSearchService searchService, TeamIterationGetByIdService getService) {
        this.searchService = searchService;
        this.getService = getService;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("team_iteration_search", "Search for team iterations (sprints).",
                schema.object().prop("nameQuery", schema.string()).prop("teamId", schema.integer()).prop("teamName", schema.string())
                        .prop("startDate", schema.string()).prop("endDate", schema.string()).prop("take", schema.integer().withDefault(10)).build(),
                args -> search(new TeamIterationSearchService.SearchCriteria(args.path("nameQuery").asText(null), args.has("teamId") ? args.path("teamId").asInt() : null,
                        args.path("teamName").asText(null), args.path("startDate").asText(null), args.path("endDate").asText(null), args.path("take").asInt(10))));

        server.registerTool("team_iteration_get", "Get a team iteration by ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> get(args.path("id").asInt()));
    }

    private String search(TeamIterationSearchService.SearchCriteria criteria) {
        var results = searchService.search(criteria);
        return results.isEmpty() ? "No sprints found." : String.join("\n", results.stream().map(t -> "[%d] %s (Team: %s, Start: %s, End: %s)".formatted(t.id(), t.name(), t.teamName(), t.startDate(), t.endDate())).toList());
    }

    private String get(int id) {
        var team = getService.get(id);
        return "[%d] %s (Team: %s, Start: %s, End: %s)".formatted(team.id(), team.name(), team.teamName(), team.startDate(), team.endDate());
    }
}
