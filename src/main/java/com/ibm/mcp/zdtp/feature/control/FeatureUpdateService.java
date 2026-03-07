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

    public FeatureUpdateService(TargetProcessProperties props, TargetProcessHttpClient http, FeatureConverter conv, ObjectMapper mapper) {
        super(props, http, mapper); this.converter = conv;
    }

    public FeatureDto updateFeature(int id, String name, String description, String stateName, Double effort) {
        return update(id, name, description, stateName, effort);
    }

    public FeatureDto update(int id, String name, String description, String stateName, Double effort) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) body.put("Name", name);
        if (description != null) body.put("Description", description);
        if (stateName != null && !stateName.isBlank()) body.put("EntityState", Map.of("Name", stateName));
        if (effort != null) body.put("Effort", effort);
        return engine.update(QueryEngine.FEATURE, id, body, converter::toDto, Feature.class);
    }
}
