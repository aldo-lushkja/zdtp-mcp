package com.ibm.mcp.zdtp.feature.control;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;

public class FeatureUpdateService extends BaseService {
    private final FeatureConverter converter;

    public FeatureUpdateService(QueryEngine engine, FeatureConverter converter) {
        super(engine);
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
            bodyMap.put("Description", convertMarkdown(description));
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


