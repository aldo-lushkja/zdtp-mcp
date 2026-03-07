package com.ibm.mcp.zdtp.release.control;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.release.entity.Release;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;

public class ReleaseCreateService extends BaseService {
    private final ReleaseConverter converter;

    public ReleaseCreateService(QueryEngine engine, ReleaseConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public ReleaseDto createRelease(String name, int projectId, String description, Double effort) {
        return create(name, projectId, description, effort);
    }

    public ReleaseDto create(String name, int projectId, String description, Double effort) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("Name", name);
        bodyMap.put("Project", Map.of("Id", projectId));
        
        if (description != null && !description.isBlank()) {
            bodyMap.put("Description", description);
        }
        
        if (effort != null) {
            bodyMap.put("Effort", effort);
        }
        
        return engine.create(QueryEngine.RELEASE, bodyMap, converter::toDto, Release.class);
    }
}

