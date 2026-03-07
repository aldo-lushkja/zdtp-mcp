package com.ibm.mcp.zdtp.request.control;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.request.entity.Request;
import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.shared.control.*;

public class RequestUpdateService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";
    private final RequestConverter converter;
    private final ObjectMapper objectMapper;

    public RequestUpdateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, RequestConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient);
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public RequestDto updateRequest(int id, String name, String description, String stateName, Double effort) {
        return update(id, name, description, stateName, effort);
    }

    public RequestDto update(int id, String name, String description, String stateName, Double effort) {
        try {
            Map<String, Object> bodyMap = new LinkedHashMap<>();
            if (name != null && !name.isBlank()) bodyMap.put("Name", name);
            if (description != null) bodyMap.put("Description", description);
            if (stateName != null && !stateName.isBlank()) bodyMap.put("EntityState", Map.of("Name", stateName));
            if (effort != null) bodyMap.put("Effort", effort);
            
            String jsonBody = bodyMap.isEmpty() ? "{}" : objectMapper.writeValueAsString(bodyMap);
            Map<String, String> parameters = new TreeMap<>();
            parameters.put("include", INCLUDE);
            return postSingle("Requests/" + id, parameters, jsonBody, Request.class, converter::toDto);
        } catch (Exception e) {
            if (e instanceof TargetProcessApiException) throw (TargetProcessApiException) e;
            throw new TargetProcessClientException("Failed to serialize update request body", e);
        }
    }
}
