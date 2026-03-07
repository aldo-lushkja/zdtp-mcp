package com.ibm.mcp.zdtp.feature.control;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.shared.control.*;

public class FeatureUpdateService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";
    private final FeatureConverter converter;
    private final ObjectMapper objectMapper;

    public FeatureUpdateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, FeatureConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient);
        this.converter = converter;
        this.objectMapper = objectMapper;
    }

    public FeatureDto updateFeature(int id, String name, String description, String stateName, Double effort) {
        return update(id, name, description, stateName, effort);
    }

    public FeatureDto update(int id, String name, String description, String stateName, Double effort) {
        try {
            Map<String, Object> bodyMap = new LinkedHashMap<>();
            if (name != null && !name.isBlank()) bodyMap.put("Name", name);
            if (description != null) bodyMap.put("Description", description);
            if (stateName != null && !stateName.isBlank()) bodyMap.put("EntityState", Map.of("Name", stateName));
            if (effort != null) bodyMap.put("Effort", effort);
            
            String jsonBody = bodyMap.isEmpty() ? "{}" : objectMapper.writeValueAsString(bodyMap);
            Map<String, String> parameters = new TreeMap<>();
            parameters.put("include", INCLUDE);
            return postSingle("Features/" + id, parameters, jsonBody, Feature.class, converter::toDto);
        } catch (Exception e) {
            if (e instanceof TargetProcessApiException) throw (TargetProcessApiException) e;
            throw new TargetProcessClientException("Failed to serialize update request body", e);
        }
    }
}
