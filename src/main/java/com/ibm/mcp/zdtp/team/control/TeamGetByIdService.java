package com.ibm.mcp.zdtp.team.control;

import java.util.TreeMap;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.team.entity.Team;
import com.ibm.mcp.zdtp.team.entity.TeamDto;

public class TeamGetByIdService extends BaseService {
    private final TeamConverter converter;

    public TeamGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TeamConverter converter) {
        super(properties, httpClient);
        this.converter = converter;
    }

    public TeamDto getById(int id) {
        return get(id);
    }

    public TeamDto get(int id) {
        return fetchSingle("Teams/" + id, new TreeMap<>(), Team.class, converter::toDto);
    }
}
