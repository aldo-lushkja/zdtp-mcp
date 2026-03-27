package com.ibm.mcp.zdtp.request.control;


import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.request.entity.Request;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;

public class RequestGetByIdService extends BaseService {
    private final RequestConverter converter;

    public RequestGetByIdService(QueryEngine engine, RequestConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public RequestDto getById(int id) {
        return get(id);
    }

    public RequestDto get(int id) {
        return engine.get(QueryEngine.REQUEST, id, converter::toDto, Request.class);
    }
}

