package com.ibm.mcp.zdtp.team.control;


import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.team.entity.Team;
import com.ibm.mcp.zdtp.team.entity.TeamDto;

public class TeamGetByIdService extends BaseService {
    private final TeamConverter converter;

    public TeamGetByIdService(QueryEngine engine, TeamConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public TeamDto getById(int id) {
        return get(id);
    }

    public TeamDto get(int id) {
        return engine.get(QueryEngine.TEAM, id, converter::toDto, Team.class);
    }
}

