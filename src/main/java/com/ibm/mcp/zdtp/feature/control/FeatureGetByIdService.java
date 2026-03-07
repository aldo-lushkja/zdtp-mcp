package com.ibm.mcp.zdtp.feature.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class FeatureGetByIdService extends BaseService {
    private final FeatureConverter converter;

    public FeatureGetByIdService(TargetProcessProperties props, TargetProcessHttpClient http, FeatureConverter conv, ObjectMapper mapper) {
        super(props, http, mapper); this.converter = conv;
    }

    public FeatureDto getById(int id) {
        return get(id);
    }

    public FeatureDto get(int id) {
        return engine.get(QueryEngine.FEATURE, id, converter::toDto, Feature.class);
    }
}
