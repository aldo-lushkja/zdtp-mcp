package com.ibm.mcp.zdtp.relation.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.relation.entity.RelationDto;

public class RelationSearchService extends BaseService {
    private final RelationConverter converter;

    public RelationSearchService(QueryEngine engine, RelationConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public List<RelationDto> findByEntity(int entityId) {
        String whereClause = "(InboundGeneral.Id eq %d) or (OutboundGeneral.Id eq %d)".formatted(entityId, entityId);
        
        Map<String, String> parameters = new TreeMap<>();
        parameters.put("where", whereClause);

        return engine.list(QueryEngine.RELATION, parameters, new TypeReference<>() {}, converter::toDto);
    }
}
