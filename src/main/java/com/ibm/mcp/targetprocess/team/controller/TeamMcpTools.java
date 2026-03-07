package com.ibm.mcp.targetprocess.team.controller;

import com.ibm.mcp.targetprocess.team.dto.TeamDto;
import com.ibm.mcp.targetprocess.team.service.TeamGetByIdService;
import com.ibm.mcp.targetprocess.team.service.TeamSearchService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamMcpTools {

    private final TeamSearchService teamSearchService;
    private final TeamGetByIdService teamGetByIdService;

    public TeamMcpTools(TeamSearchService teamSearchService,
                        TeamGetByIdService teamGetByIdService) {
        this.teamSearchService = teamSearchService;
        this.teamGetByIdService = teamGetByIdService;
    }

    @Tool(description = """
            Search for teams in Targetprocess. \
            Supports filtering by team name. \
            Results are ordered by name.""")
    public String searchTeams(String nameQuery, int take) {
        List<TeamDto> teams = teamSearchService.searchTeams(nameQuery, take);
        if (teams.isEmpty()) {
            return "No teams found.";
        }
        return String.join("\n", teams.stream().map(this::format).toList());
    }

    @Tool(description = "Get a team by its numeric ID.")
    public String getTeamById(int id) {
        TeamDto team = teamGetByIdService.getById(id);
        return format(team);
    }

    private String format(TeamDto t) {
        return "[%d] %s".formatted(t.id(), t.name());
    }
}