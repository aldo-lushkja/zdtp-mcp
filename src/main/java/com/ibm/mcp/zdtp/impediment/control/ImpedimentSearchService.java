package com.ibm.mcp.zdtp.impediment.control;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.impediment.entity.ImpedimentDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ImpedimentSearchService extends BaseService {
    private final ImpedimentConverter converter;

    public ImpedimentSearchService(QueryEngine engine, ImpedimentConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public List<ImpedimentDto> search(int entityId) {
        String whereClause = query()
                .add("Assignable.Id eq %d".formatted(entityId))
                .build();

        Map<String, String> parameters = new TreeMap<>();
        if (!whereClause.isBlank()) {
            parameters.put("where", whereClause);
        }

        return engine.list(QueryEngine.IMPEDIMENT, parameters, new TypeReference<>() {}, converter::toDto);
    }
}
