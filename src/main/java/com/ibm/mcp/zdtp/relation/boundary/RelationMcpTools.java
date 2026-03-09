package com.ibm.mcp.zdtp.relation.boundary;

import com.ibm.mcp.zdtp.relation.control.RelationCreateService;
import com.ibm.mcp.zdtp.relation.control.RelationSearchService;
import com.ibm.mcp.zdtp.relation.entity.RelationDto;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class RelationMcpTools {
    private final RelationSearchService searchSvc;
    private final RelationCreateService createSvc;

    public RelationMcpTools(RelationSearchService searchSvc, RelationCreateService createSvc) {
        this.searchSvc = searchSvc;
        this.createSvc = createSvc;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("relation_search", "Find all relations (links, blockers, dependencies, etc.) for a specific entity.",
                schema.object()
                        .prop("entityId", schema.integer().required().withDescription("ID of the entity (User Story, Bug, Feature, etc.) to find relations for."))
                        .build(),
                args -> search(args.path("entityId").asInt()));

        server.registerTool("relation_link", "Create a relation between two entities. Valid types: Relation, Dependency, Blocker, Link, Duplicate.",
                schema.object()
                        .prop("masterId", schema.integer().required().withDescription("ID of the master (from) entity."))
                        .prop("slaveId",  schema.integer().required().withDescription("ID of the slave (to) entity."))
                        .prop("typeName", schema.string().withDefault("Relation").withDescription("Relation type: Relation, Dependency, Blocker, Link, or Duplicate."))
                        .build(),
                args -> link(args.path("masterId").asInt(), args.path("slaveId").asInt(), args.path("typeName").asText("Relation")));
    }

    private String search(int entityId) {
        List<RelationDto> relations = searchSvc.findByEntity(entityId);
        if (relations.isEmpty()) return "No relations found for entity [" + entityId + "].";
        return String.join("\n", relations.stream()
                .map(r -> "[%d] %s (%s) -[%s]-> %s (%s)".formatted(
                        r.id(), r.masterName(), r.masterType(), r.typeName(), r.slaveName(), r.slaveType()))
                .toList());
    }

    private String link(int masterId, int slaveId, String typeName) {
        RelationDto rel = createSvc.link(masterId, slaveId, typeName);
        return "Relation [%d] created: %s (%s) -[%s]-> %s (%s)"
                .formatted(rel.id(), rel.masterName(), rel.masterType(), rel.typeName(), rel.slaveName(), rel.slaveType());
    }
}
