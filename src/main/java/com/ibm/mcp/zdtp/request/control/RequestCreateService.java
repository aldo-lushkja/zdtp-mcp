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

public class RequestCreateService extends BaseService {
    private final RequestConverter converter;

    public RequestCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, RequestConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public RequestDto createRequest(String name, int projectId, String description, Double effort) {
        return create(name, projectId, description, effort);
    }

    public RequestDto create(String name, int projectId, String description, Double effort) {
        Map<String, Object> body = new LinkedHashMap<>(); body.put("Name", name); body.put("Project", Map.of("Id", projectId));
        if (description != null && !description.isBlank()) body.put("Description", description);
        if (effort != null) body.put("Effort", effort);
        return engine.create(QueryEngine.REQUEST, body, converter::toDto, Request.class);
    }
}
