package com.ibm.mcp.zdtp.epic.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.epic.entity.Epic;
import com.ibm.mcp.zdtp.epic.entity.EpicDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class EpicCreateService extends BaseService {
    private final EpicConverter converter;

    public EpicCreateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, EpicConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public EpicDto createEpic(String name, int projectId, String description, Double effort) {
        return create(name, projectId, description, effort);
    }

    public EpicDto create(String name, int projectId, String description, Double effort) {
        Map<String, Object> body = new LinkedHashMap<>(); body.put("Name", name); body.put("Project", Map.of("Id", projectId));
        if (description != null && !description.isBlank()) body.put("Description", description);
        if (effort != null) body.put("Effort", effort);
        return engine.create(QueryEngine.EPIC, body, converter::toDto, Epic.class);
    }
}
