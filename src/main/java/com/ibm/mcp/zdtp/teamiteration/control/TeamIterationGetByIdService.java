package com.ibm.mcp.zdtp.teamiteration.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIteration;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIterationDto;

public class TeamIterationGetByIdService extends BaseService {
    private final TeamIterationConverter converter;

    public TeamIterationGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TeamIterationConverter converter, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
        this.converter = converter;
    }

    public TeamIterationDto getById(int id) {
        return get(id);
    }

    public TeamIterationDto get(int id) {
        return engine.get(QueryEngine.TEAM_ITERATION, id, converter::toDto, TeamIteration.class);
    }
}
