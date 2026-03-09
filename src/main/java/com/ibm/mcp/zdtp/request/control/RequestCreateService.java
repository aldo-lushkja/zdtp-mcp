package com.ibm.mcp.zdtp.request.control;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.request.entity.Request;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;

public class RequestCreateService extends BaseService {
    private final RequestConverter converter;

    public RequestCreateService(QueryEngine engine, RequestConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public RequestDto createRequest(String name, int projectId, String description, Double effort) {
        return create(name, projectId, description, effort);
    }

    public RequestDto create(String name, int projectId, String description, Double effort) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("Name", name);
        bodyMap.put("Project", Map.of("Id", projectId));
        
        if (description != null && !description.isBlank()) {
            bodyMap.put("Description", convertMarkdown(description));
        }
        
        if (effort != null) {
            bodyMap.put("Effort", effort);
        }
        
        return engine.create(QueryEngine.REQUEST, bodyMap, converter::toDto, Request.class);
    }
}


