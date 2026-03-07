package com.ibm.mcp.zdtp.team.boundary;

import com.ibm.mcp.zdtp.team.entity.TeamDto;
import com.ibm.mcp.zdtp.team.control.TeamGetByIdService;
import com.ibm.mcp.zdtp.team.control.TeamSearchService;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class TeamMcpTools {

    private final TeamSearchService teamSearchService;
    private final TeamGetByIdService teamGetByIdService;

    public TeamMcpTools(TeamSearchService teamSearchService,
                        TeamGetByIdService teamGetByIdService) {
        this.teamSearchService = teamSearchService;
        this.teamGetByIdService = teamGetByIdService;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("team_search", 
            "Search for teams in Targetprocess. Supports filtering by team name. Results are ordered by name.",
            schema.object()
                .prop("nameQuery", schema.string())
                .prop("take", schema.integer().withDefault(10))
                .build(),
            args -> searchTeams(
                args.path("nameQuery").asText(null),
                args.path("take").asInt(10)
            )
        );

        server.registerTool("team_get",
            "Get a team by its numeric ID.",
            schema.object()
                .prop("id", schema.integer().required())
                .build(),
            args -> getTeamById(args.path("id").asInt())
        );
    }

    public String searchTeams(String nameQuery, int take) {
        List<TeamDto> teams = teamSearchService.searchTeams(nameQuery, take);
        if (teams.isEmpty()) {
            return "No teams found.";
        }
        return String.join("\n", teams.stream().map(this::format).toList());
    }

    public String getTeamById(int id) {
        TeamDto team = teamGetByIdService.getById(id);
        return format(team);
    }

    private String format(TeamDto t) {
        return "[%d] %s".formatted(t.id(), t.name());
    }
}