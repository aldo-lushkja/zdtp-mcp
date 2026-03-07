package com.ibm.mcp.zdtp.request.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.request.entity.Request;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class RequestUpdateService extends BaseService {
    private final RequestConverter converter;

    public RequestUpdateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, RequestConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public RequestDto updateRequest(int id, String name, String description, String stateName, Double effort) {
        return update(id, name, description, stateName, effort);
    }

    public RequestDto update(int id, String name, String description, String stateName, Double effort) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) body.put("Name", name);
        if (description != null) body.put("Description", description);
        if (stateName != null && !stateName.isBlank()) body.put("EntityState", Map.of("Name", stateName));
        if (effort != null) body.put("Effort", effort);
        return engine.update(QueryEngine.REQUEST, id, body, converter::toDto, Request.class);
    }
}
