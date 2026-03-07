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

public class EpicUpdateService extends BaseService {
    private final EpicConverter converter;

    public EpicUpdateService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, EpicConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public EpicDto updateEpic(int id, String name, String description, String stateName, Double effort) {
        return update(id, name, description, stateName, effort);
    }

    public EpicDto update(int id, String name, String description, String stateName, Double effort) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) body.put("Name", name);
        if (description != null) body.put("Description", description);
        if (stateName != null && !stateName.isBlank()) body.put("EntityState", Map.of("Name", stateName));
        if (effort != null) body.put("Effort", effort);
        return engine.update(QueryEngine.EPIC, id, body, converter::toDto, Epic.class);
    }
}
