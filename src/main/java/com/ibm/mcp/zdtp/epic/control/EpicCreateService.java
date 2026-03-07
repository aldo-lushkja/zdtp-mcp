package com.ibm.mcp.zdtp.epic.control;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.epic.entity.Epic;
import com.ibm.mcp.zdtp.epic.entity.EpicDto;
import com.ibm.mcp.zdtp.shared.control.*;

public class EpicCreateService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";
    private final EpicConverter converter;
    private final ObjectMapper objectMapper;

    public EpicCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, EpicConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient);
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public EpicDto createEpic(String name, int projectId, String description, Double effort) {
        return create(name, projectId, description, effort);
    }

    public EpicDto create(String name, int projectId, String description, Double effort) {
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
            return postSingle("Epics", parameters, jsonBody, Epic.class, converter::toDto);
        } catch (Exception e) {
            if (e instanceof TargetProcessApiException) throw (TargetProcessApiException) e;
            throw new TargetProcessClientException("Failed to serialize create request body", e);
        }
    }
}
