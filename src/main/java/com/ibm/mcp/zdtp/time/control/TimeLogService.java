package com.ibm.mcp.zdtp.time.control;

import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.time.entity.TimeEntry;
import com.ibm.mcp.zdtp.time.entity.TimeEntryDto;

import java.util.LinkedHashMap;
import java.util.Map;

public class TimeLogService extends BaseService {
    private final TimeConverter converter;

    public TimeLogService(QueryEngine engine, TimeConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public TimeEntryDto logTime(int entityId, double spent, String description) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Spent", spent);
        body.put("Assignable", Map.of("Id", entityId));
        if (description != null && !description.isBlank()) {
            body.put("Description", description);
        }
        return engine.create(QueryEngine.TIME, body, converter::toDto, TimeEntry.class);
    }
}
