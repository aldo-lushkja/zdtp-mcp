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

    public FeatureCreateService(TargetProcessProperties props, TargetProcessHttpClient http, FeatureConverter conv, ObjectMapper mapper) {
        super(props, http, mapper); this.converter = conv;
    }

    public FeatureDto createFeature(String name, int projectId, String description, Double effort) {
        return create(name, projectId, description, effort);
    }

    public FeatureDto create(String name, int projectId, String description, Double effort) {
        Map<String, Object> body = new LinkedHashMap<>(); body.put("Name", name); body.put("Project", Map.of("Id", projectId));
        if (description != null && !description.isBlank()) body.put("Description", description);
        if (effort != null) body.put("Effort", effort);
        return engine.create(QueryEngine.FEATURE, body, converter::toDto, Feature.class);
    }
}
