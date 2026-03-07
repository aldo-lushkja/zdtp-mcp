package com.ibm.mcp.zdtp.team.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.team.entity.Team;
import com.ibm.mcp.zdtp.team.entity.TeamDto;

public class TeamSearchService extends BaseService {
    private final TeamConverter converter;

    public TeamSearchService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TeamConverter converter, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
        this.converter = converter;
    }

    public record SearchCriteria(String nameQuery, int take) {}

    public List<TeamDto> searchTeams(String nameQuery, int take) {
        return search(new SearchCriteria(nameQuery, take));
    }

    public List<TeamDto> search(SearchCriteria criteria) {
        String whereClause = query()
                .add("Name", "contains", criteria.nameQuery())
                .build();

        Map<String, String> parameters = new TreeMap<>();
        if (!whereClause.isBlank()) {
            parameters.put("where", whereClause);
        }
        parameters.put("orderBy", "Name");
        parameters.put("take", String.valueOf(criteria.take()));

        return engine.list(QueryEngine.TEAM, parameters, new TypeReference<>() {}, converter::toDto);
    }
}
