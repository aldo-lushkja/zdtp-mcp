package com.ibm.mcp.zdtp.feature.control;

import java.util.Map;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class FeatureGetByIdService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";
    private final FeatureConverter converter;

    public FeatureGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, FeatureConverter converter) {
        super(properties, httpClient);
        this.converter = converter;
    }

    public FeatureDto getById(int id) {
        return get(id);
    }

    public FeatureDto get(int id) {
        Map<String, String> parameters = Map.of("include", INCLUDE);
        return fetchSingle("Features/" + id, parameters, Feature.class, converter::toDto);
    }
}
