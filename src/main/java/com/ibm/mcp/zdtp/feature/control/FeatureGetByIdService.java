package com.ibm.mcp.zdtp.feature.control;


import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;

public class FeatureGetByIdService extends BaseService {
    private final FeatureConverter converter;

    public FeatureGetByIdService(QueryEngine engine, FeatureConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public FeatureDto getById(int id) {
        return get(id);
    }

    public FeatureDto get(int id) {
        return engine.get(QueryEngine.FEATURE, id, converter::toDto, Feature.class);
    }
}

