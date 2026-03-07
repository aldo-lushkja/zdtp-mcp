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

public class ReleaseCreateService extends BaseService {
    private final ReleaseConverter converter;

    public ReleaseCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, ReleaseConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public ReleaseDto createRelease(String name, int projectId, String description, Double effort) {
        return create(name, projectId, description, effort);
    }

    public ReleaseDto create(String name, int projectId, String description, Double effort) {
        Map<String, Object> body = new LinkedHashMap<>(); body.put("Name", name); body.put("Project", Map.of("Id", projectId));
        if (description != null && !description.isBlank()) body.put("Description", description);
        if (effort != null) body.put("Effort", effort);
        return engine.create(QueryEngine.RELEASE, body, converter::toDto, Release.class);
    }
}
