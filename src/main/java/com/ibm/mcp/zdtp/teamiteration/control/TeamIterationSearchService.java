package com.ibm.mcp.zdtp.teamiteration.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIteration;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIterationDto;

public class TeamIterationSearchService extends BaseService {
    private final TeamIterationConverter converter;

    public TeamIterationSearchService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TeamIterationConverter converter, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
        this.converter = converter;
    }

    public record SearchCriteria(String nameQuery, Integer teamId, String teamName, String startDate, String endDate, int take) {}

    public List<TeamIterationDto> searchTeamIterations(String nameQuery, Integer teamId, String teamName, String startDate, String endDate, int take) {
        return search(new SearchCriteria(nameQuery, teamId, teamName, startDate, endDate, take));
    }

    public List<TeamIterationDto> search(SearchCriteria criteria) {
        String whereClause = query()
                .add("Name", "contains", criteria.nameQuery())
                .add(criteria.teamId() != null && criteria.teamId() > 0 ? "Team.Id eq %d".formatted(criteria.teamId()) : null)
                .add("Team.Name", "contains", criteria.teamName())
                .add(criteria.startDate() != null && !criteria.startDate().isBlank() ? "StartDate gte '%s'".formatted(criteria.startDate()) : null)
                .build();

        Map<String, String> parameters = new TreeMap<>();
        if (!whereClause.isBlank()) {
            parameters.put("where", whereClause);
        }
        parameters.put("orderByDesc", "StartDate");
        parameters.put("take", String.valueOf(criteria.take()));

        return engine.list(QueryEngine.TEAM_ITERATION, parameters, new TypeReference<>() {}, converter::toDto);
    }
}
