package com.ibm.mcp.targetprocess.teamiteration.controller;

import com.ibm.mcp.targetprocess.teamiteration.dto.TeamIterationDto;
import com.ibm.mcp.targetprocess.teamiteration.service.TeamIterationGetByIdService;
import com.ibm.mcp.targetprocess.teamiteration.service.TeamIterationSearchService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamIterationMcpTools {

    private final TeamIterationSearchService searchService;
    private final TeamIterationGetByIdService getByIdService;

    public TeamIterationMcpTools(TeamIterationSearchService searchService,
                                  TeamIterationGetByIdService getByIdService) {
        this.searchService = searchService;
        this.getByIdService = getByIdService;
    }

    @Tool(description = """
            Search for team iterations (sprints) in Targetprocess. \
            Supports filtering by name, teamId (numeric team ID), teamName, \
            and date range (startDate/endDate in YYYY-MM-DD format, filters on StartDate). \
            Results are ordered by start date descending.""")
    public String searchTeamIterations(String nameQuery, Integer teamId, String teamName,
                                        String startDate, String endDate, int take) {
        List<TeamIterationDto> iterations = searchService.searchTeamIterations(
                nameQuery, teamId, teamName, startDate, endDate, take);
        if (iterations.isEmpty()) {
            return "No team iterations found.";
        }
        return String.join("\n", iterations.stream().map(this::format).toList());
    }

    @Tool(description = "Get a team iteration by its numeric ID. Returns full details.")
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