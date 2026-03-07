package com.ibm.mcp.zdtp.request.control;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.request.entity.Request;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;

public class RequestUpdateService extends BaseService {
    private final RequestConverter converter;

    public RequestUpdateService(QueryEngine engine, RequestConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public RequestDto updateRequest(int id, String name, String description, String stateName, Double effort) {
        return update(id, name, description, stateName, effort);
    }

    public RequestDto update(int id, String name, String description, String stateName, Double effort) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) {
            bodyMap.put("Name", name);
        }
        if (description != null) {
            bodyMap.put("Description", convertMarkdown(description));
        }
        if (stateName != null && !stateName.isBlank()) {
            bodyMap.put("EntityState", Map.of("Name", stateName));
        }
        if (effort != null) {
            bodyMap.put("Effort", effort);
        }
        
        return engine.update(QueryEngine.REQUEST, id, bodyMap, converter::toDto, Request.class);
    }
}


