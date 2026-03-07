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

    public TeamIterationSearchService(TargetProcessProperties props, TargetProcessHttpClient http, TeamIterationConverter conv, ObjectMapper mapper) {
        super(props, http, mapper); this.converter = conv;
    }

    public record SearchCriteria(String nameQuery, Integer teamId, String teamName, String startDate, String endDate, int take) {}

    public List<TeamIterationDto> searchTeamIterations(String nameQuery, Integer teamId, String teamName, String startDate, String endDate, int take) {
        return search(new SearchCriteria(nameQuery, teamId, teamName, startDate, endDate, take));
    }

    public List<TeamIterationDto> search(SearchCriteria criteria) {
        String where = query().add("Name", "contains", criteria.nameQuery()).add(criteria.teamId() != null && criteria.teamId() > 0 ? "Team.Id eq %d".formatted(criteria.teamId()) : null)
                .add("Team.Name", "contains", criteria.teamName()).add(criteria.startDate() != null && !criteria.startDate().isBlank() ? "StartDate gte '%s'".formatted(criteria.startDate()) : null).build();
        Map<String, String> p = new TreeMap<>(); if (!where.isBlank()) p.put("where", where);
        p.put("orderByDesc", "StartDate"); p.put("take", String.valueOf(criteria.take()));
        return engine.list(QueryEngine.TEAM_ITERATION, p, new TypeReference<>() {}, converter::toDto);
    }
}
