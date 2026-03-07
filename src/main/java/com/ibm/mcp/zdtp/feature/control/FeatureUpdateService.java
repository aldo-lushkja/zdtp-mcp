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

public class FeatureUpdateService extends BaseService {
    private final FeatureConverter converter;

    public FeatureUpdateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, FeatureConverter converter, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
        this.converter = converter;
    }

    public FeatureDto updateFeature(int id, String name, String description, String stateName, Double effort) {
        return update(id, name, description, stateName, effort);
    }

    public FeatureDto update(int id, String name, String description, String stateName, Double effort) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) {
            bodyMap.put("Name", name);
        }
        if (description != null) {
            bodyMap.put("Description", description);
        }
        if (stateName != null && !stateName.isBlank()) {
            bodyMap.put("EntityState", Map.of("Name", stateName));
        }
        if (effort != null) {
            bodyMap.put("Effort", effort);
        }
        
        return engine.update(QueryEngine.FEATURE, id, bodyMap, converter::toDto, Feature.class);
    }
}
