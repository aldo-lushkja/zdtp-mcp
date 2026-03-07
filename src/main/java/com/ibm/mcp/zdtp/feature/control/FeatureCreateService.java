package com.ibm.mcp.zdtp.feature.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class FeatureCreateService extends BaseService {
    private final FeatureConverter converter;

    public FeatureCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, FeatureConverter converter, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
        this.converter = converter;
    }

    public FeatureDto createFeature(String name, int projectId, String description, Double effort) {
        return create(name, projectId, description, effort);
    }

    public FeatureDto create(String name, int projectId, String description, Double effort) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("Name", name);
        bodyMap.put("Project", Map.of("Id", projectId));
        
        if (description != null && !description.isBlank()) {
            bodyMap.put("Description", description);
        }
        
        if (effort != null) {
            bodyMap.put("Effort", effort);
        }
        
        return engine.create(QueryEngine.FEATURE, bodyMap, converter::toDto, Feature.class);
    }
}
