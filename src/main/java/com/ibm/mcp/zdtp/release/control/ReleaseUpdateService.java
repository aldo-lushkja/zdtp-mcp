package com.ibm.mcp.zdtp.release.control;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.release.entity.Release;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;

public class ReleaseUpdateService extends BaseService {
    private final ReleaseConverter converter;

    public ReleaseUpdateService(QueryEngine engine, ReleaseConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public ReleaseDto updateRelease(int id, String name, String description, String stateName, Double effort) {
        return update(id, name, description, stateName, effort);
    }

    public ReleaseDto update(int id, String name, String description, String stateName, Double effort) {
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
        
        return engine.update(QueryEngine.RELEASE, id, bodyMap, converter::toDto, Release.class);
    }
}


