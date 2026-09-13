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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private TeamIterationSearchService searchService;
        private TeamIterationGetByIdService getService;

        public Builder searchService(TeamIterationSearchService searchService) { this.searchService = searchService; return this; }
        public Builder getService(TeamIterationGetByIdService getService) { this.getService = getService; return this; }

        public TeamIterationMcpTools build() {
            return new TeamIterationMcpTools(searchService, getService);
        }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("team_iteration_search", "Search for team iterations (sprints).",
                schema.object().prop("nameQuery", schema.string()).prop("teamId", schema.integer()).prop("teamName", schema.string())
                        .prop("startDate", schema.string()).prop("endDate", schema.string()).prop("take", schema.integer().withDefault(10)).build(),
                args -> search(TeamIterationSearchService.SearchCriteria.builder()
                        .nameQuery(args.path("nameQuery").asText(null))
                        .teamId(args.has("teamId") ? args.path("teamId").asInt() : null)
                        .teamName(args.path("teamName").asText(null))
                        .startDate(args.path("startDate").asText(null))
                        .endDate(args.path("endDate").asText(null))
                        .take(args.path("take").asInt(10))
                        .build()));

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

