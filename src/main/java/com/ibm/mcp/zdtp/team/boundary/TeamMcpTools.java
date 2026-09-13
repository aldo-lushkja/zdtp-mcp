package com.ibm.mcp.zdtp.team.boundary;

import com.ibm.mcp.zdtp.team.control.TeamGetByIdService;
import com.ibm.mcp.zdtp.team.control.TeamSearchService;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class TeamMcpTools {
    private final TeamSearchService searchService;
    private final TeamGetByIdService getService;

    public TeamMcpTools(TeamSearchService searchService, TeamGetByIdService getService) {
        this.searchService = searchService;
        this.getService = getService;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private TeamSearchService searchService;
        private TeamGetByIdService getService;

        public Builder searchService(TeamSearchService searchService) { this.searchService = searchService; return this; }
        public Builder getService(TeamGetByIdService getService) { this.getService = getService; return this; }

        public TeamMcpTools build() {
            return new TeamMcpTools(searchService, getService);
        }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("team_search", "Search for teams.",
                schema.object().prop("nameQuery", schema.string()).prop("take", schema.integer().withDefault(10)).build(),
                args -> search(TeamSearchService.SearchCriteria.builder()
                        .nameQuery(args.path("nameQuery").asText(null))
                        .take(args.path("take").asInt(10))
                        .build()));

        server.registerTool("team_get", "Get a team by ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> get(args.path("id").asInt()));
    }

    private String search(TeamSearchService.SearchCriteria criteria) {
        var results = searchService.search(criteria);
        return results.isEmpty() ? "No teams found." : String.join("\n", results.stream().map(t -> "[%d] %s".formatted(t.id(), t.name())).toList());
    }

    private String get(int id) {
        var team = getService.get(id);
        return "[%d] %s".formatted(team.id(), team.name());
    }
}

