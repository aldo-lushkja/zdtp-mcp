package com.ibm.mcp.zdtp.epic.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.epic.entity.Epic;
import com.ibm.mcp.zdtp.epic.entity.EpicDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class EpicGetByIdService extends BaseService {
    private final EpicConverter converter;

    public EpicGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, EpicConverter converter, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
        this.converter = converter;
    }

    public EpicDto getById(int id) {
        return get(id);
    }

    public EpicDto get(int id) {
        return engine.get(QueryEngine.EPIC, id, converter::toDto, Epic.class);
    }
}
