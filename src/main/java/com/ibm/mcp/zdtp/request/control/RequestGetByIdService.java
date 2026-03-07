package com.ibm.mcp.zdtp.request.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.request.entity.Request;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class RequestGetByIdService extends BaseService {
    private final RequestConverter converter;

    public RequestGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, RequestConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public RequestDto getById(int id) {
        return get(id);
    }

    public RequestDto get(int id) {
        return engine.get(QueryEngine.REQUEST, id, converter::toDto, Request.class);
    }
}
