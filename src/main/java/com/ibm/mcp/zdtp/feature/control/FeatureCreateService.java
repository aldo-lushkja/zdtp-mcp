package com.ibm.mcp.zdtp.feature.control;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;

public class FeatureCreateService extends BaseService {
    private final FeatureConverter converter;

    public FeatureCreateService(QueryEngine engine, FeatureConverter converter) {
        super(engine);
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

