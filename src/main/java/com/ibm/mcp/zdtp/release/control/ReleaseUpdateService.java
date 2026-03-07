package com.ibm.mcp.zdtp.release.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.release.entity.Release;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class ReleaseUpdateService extends BaseService {
    private final ReleaseConverter converter;

    public ReleaseUpdateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, ReleaseConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public ReleaseDto updateRelease(int id, String name, String description, String stateName, Double effort) {
        return update(id, name, description, stateName, effort);
    }

    public ReleaseDto update(int id, String name, String description, String stateName, Double effort) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) body.put("Name", name);
        if (description != null) body.put("Description", description);
        if (stateName != null && !stateName.isBlank()) body.put("EntityState", Map.of("Name", stateName));
        if (effort != null) body.put("Effort", effort);
        return engine.update(QueryEngine.RELEASE, id, body, converter::toDto, Release.class);
    }
}
