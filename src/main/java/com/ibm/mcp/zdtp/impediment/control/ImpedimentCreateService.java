package com.ibm.mcp.zdtp.impediment.control;

import com.ibm.mcp.zdtp.impediment.entity.Impediment;
import com.ibm.mcp.zdtp.impediment.entity.ImpedimentDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import java.util.LinkedHashMap;
import java.util.Map;

public class ImpedimentCreateService extends BaseService {
    private final ImpedimentConverter converter;

    public ImpedimentCreateService(QueryEngine engine, ImpedimentConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public ImpedimentDto create(int entityId, String name, String description) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Name", name);
        body.put("Assignable", Map.of("Id", entityId));
        if (description != null && !description.isBlank()) {
            body.put("Description", convertMarkdown(description));
        }
        return engine.create(QueryEngine.IMPEDIMENT, body, converter::toDto, Impediment.class);
    }
}
