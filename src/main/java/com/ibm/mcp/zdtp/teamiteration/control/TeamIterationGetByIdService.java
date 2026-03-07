package com.ibm.mcp.zdtp.teamiteration.control;

import java.util.TreeMap;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIteration;
import com.ibm.mcp.zdtp.teamiteration.entity.TeamIterationDto;

public class TeamIterationGetByIdService extends BaseService {
    private final TeamIterationConverter converter;

    public TeamIterationGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TeamIterationConverter converter) {
        super(properties, httpClient);
        this.converter = converter;
    }

    public TeamIterationDto getById(int id) {
        return get(id);
    }

    public TeamIterationDto get(int id) {
        return fetchSingle("TeamIterations/" + id, new TreeMap<>(), TeamIteration.class, converter::toDto);
    }
}
