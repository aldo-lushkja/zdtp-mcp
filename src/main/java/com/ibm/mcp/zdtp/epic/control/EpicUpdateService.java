package com.ibm.mcp.zdtp.epic.control;

import java.util.LinkedHashMap;
import java.util.Map;
import com.ibm.mcp.zdtp.epic.entity.Epic;
import com.ibm.mcp.zdtp.epic.entity.EpicDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class EpicUpdateService extends BaseService {
    private final EpicConverter converter;

    public EpicUpdateService(QueryEngine engine, EpicConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public EpicDto update(int id, String name, String description, String stateName, Double effort) {
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        if (name != null && !name.isBlank()) {
            bodyMap.put("Name", name);
        }
        if (description != null) {
            bodyMap.put("Description", description);
        }
        if (stateName != null && !stateName.isBlank()) {
            bodyMap.put("EntityState", Map.of("Name", stateName));
        }
        if (effort != null) {
            bodyMap.put("Effort", effort);
        }
        
        return engine.update(QueryEngine.EPIC, id, bodyMap, converter::toDto, Epic.class);
    }
}
