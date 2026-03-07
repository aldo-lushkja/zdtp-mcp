package com.ibm.mcp.zdtp.team.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.team.entity.Team;
import com.ibm.mcp.zdtp.team.entity.TeamDto;

public class TeamGetByIdService extends BaseService {
    private final TeamConverter converter;

    public TeamGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TeamConverter converter, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
        this.converter = converter;
    }

    public TeamDto getById(int id) {
        return get(id);
    }

    public TeamDto get(int id) {
        return engine.get(QueryEngine.TEAM, id, converter::toDto, Team.class);
    }
}
