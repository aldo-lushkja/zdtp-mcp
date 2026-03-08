package com.ibm.mcp.zdtp.relation.control;

import java.util.Map;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.relation.entity.Relation;
import com.ibm.mcp.zdtp.relation.entity.RelationDto;

public class RelationCreateService extends BaseService {
    private final RelationConverter converter;

    public RelationCreateService(QueryEngine engine, RelationConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public RelationDto link(int inboundId, int outboundId, String typeName) {
        Map<String, Object> body = Map.of(
            "InboundGeneral", Map.of("Id", inboundId),
            "OutboundGeneral", Map.of("Id", outboundId),
            "RelationType", Map.of("Name", typeName != null ? typeName : "Relation")
        );

        return engine.create(QueryEngine.RELATION, body, converter::toDto, Relation.class);
    }
}
