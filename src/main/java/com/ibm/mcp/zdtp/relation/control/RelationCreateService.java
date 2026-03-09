package com.ibm.mcp.zdtp.relation.control;

import java.util.Map;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.relation.entity.Relation;
import com.ibm.mcp.zdtp.relation.entity.RelationDto;

public class RelationCreateService extends BaseService {
    private final RelationConverter converter;

    // Known relation type IDs from /api/v1/RelationTypes
    private static final Map<String, Integer> TYPE_IDS = Map.of(
        "dependency", 1,
        "blocker",    2,
        "relation",   3,
        "link",       4,
        "duplicate",  5
    );

    public RelationCreateService(QueryEngine engine, RelationConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public RelationDto link(int masterId, int slaveId, String typeName) {
        int typeId = TYPE_IDS.getOrDefault(
            typeName != null ? typeName.toLowerCase() : "relation",
            3 // default: Relation
        );

        Map<String, Object> body = Map.of(
            "Master",       Map.of("Id", masterId),
            "Slave",        Map.of("Id", slaveId),
            "RelationType", Map.of("Id", typeId)
        );

        return engine.create(QueryEngine.RELATION, body, converter::toDto, Relation.class);
    }
}
