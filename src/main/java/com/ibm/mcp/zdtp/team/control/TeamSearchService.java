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

    public TeamSearchService(TargetProcessProperties props, TargetProcessHttpClient http, TeamConverter conv, ObjectMapper mapper) {
        super(props, http, mapper); this.converter = conv;
    }

    public record SearchCriteria(String nameQuery, int take) {}

    public List<TeamDto> searchTeams(String nameQuery, int take) {
        return search(new SearchCriteria(nameQuery, take));
    }

    public List<TeamDto> search(SearchCriteria criteria) {
        String where = query().add("Name", "contains", criteria.nameQuery()).build();
        Map<String, String> p = new TreeMap<>(); if (!where.isBlank()) p.put("where", where);
        p.put("orderBy", "Name"); p.put("take", String.valueOf(criteria.take()));
        return engine.list(QueryEngine.TEAM, p, new TypeReference<>() {}, converter::toDto);
    }
}
