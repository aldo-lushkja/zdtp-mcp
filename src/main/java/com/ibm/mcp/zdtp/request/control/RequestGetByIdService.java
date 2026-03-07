package com.ibm.mcp.zdtp.request.control;

import java.util.Map;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.request.entity.Request;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class RequestGetByIdService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";
    private final RequestConverter converter;

    public RequestGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, RequestConverter converter) {
        super(properties, httpClient);
        this.converter = converter;
    }

    public RequestDto getById(int id) {
        return get(id);
    }

    public RequestDto get(int id) {
        Map<String, String> parameters = Map.of("include", INCLUDE);
        return fetchSingle("Requests/" + id, parameters, Request.class, converter::toDto);
    }
}
