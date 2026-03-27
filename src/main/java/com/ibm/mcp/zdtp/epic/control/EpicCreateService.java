package com.ibm.mcp.zdtp.epic.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.ibm.mcp.zdtp.epic.entity.Epic;
import com.ibm.mcp.zdtp.epic.entity.EpicDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class EpicCreateService extends BaseService {
    private final EpicConverter converter;

    public EpicCreateService(QueryEngine engine, EpicConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public EpicDto create(String name, int projectId, String description, Double effort) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("Name", name);
        bodyMap.put("Project", Map.of("Id", projectId));
        
        if (description != null && !description.isBlank()) {
            bodyMap.put("Description", convertMarkdown(description));
        }
        
        if (effort != null) {
            bodyMap.put("Effort", effort);
        }
        
        return engine.create(QueryEngine.EPIC, bodyMap, converter::toDto, Epic.class);
    }
}

