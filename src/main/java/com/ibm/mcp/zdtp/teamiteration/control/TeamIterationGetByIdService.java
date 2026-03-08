package com.ibm.mcp.zdtp.teamiteration.control;


import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIteration;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIterationDto;

public class TeamIterationGetByIdService extends BaseService {
    private final TeamIterationConverter converter;

    public TeamIterationGetByIdService(QueryEngine engine, TeamIterationConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public TeamIterationDto getById(int id) {
        return get(id);
    }

    public TeamIterationDto get(int id) {
        return engine.get(QueryEngine.TEAM_ITERATION, id, converter::toDto, TeamIteration.class);
    }
}

