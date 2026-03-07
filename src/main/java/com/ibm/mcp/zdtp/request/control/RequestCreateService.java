package com.ibm.mcp.zdtp.request.control;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.request.entity.Request;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.shared.control.*;

public class RequestCreateService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";
    private final RequestConverter converter;
    private final ObjectMapper objectMapper;

    public RequestCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, RequestConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient);
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public RequestDto createRequest(String name, int projectId, String description, Double effort) {
        return create(name, projectId, description, effort);
    }

    public RequestDto create(String name, int projectId, String description, Double effort) {
        try {
            Map<String, Object> bodyMap = new LinkedHashMap<>();
            bodyMap.put("Name", name);
            bodyMap.put("Project", Map.of("Id", projectId));
            if (description != null && !description.isBlank()) {
                bodyMap.put("Description", description);
            }
            if (effort != null) {
                bodyMap.put("Effort", effort);
            }
            
            String jsonBody = objectMapper.writeValueAsString(bodyMap);
            Map<String, String> parameters = new TreeMap<>();
            parameters.put("include", INCLUDE);
            return postSingle("Requests", parameters, jsonBody, Request.class, converter::toDto);
        } catch (Exception e) {
            if (e instanceof TargetProcessApiException) throw (TargetProcessApiException) e;
            throw new TargetProcessClientException("Failed to serialize create request body", e);
        }
    }
}
