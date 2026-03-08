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
        server.registerTool("relation_search", "Find relations linked to a specific entity.",
                schema.object()
                        .prop("entityId", schema.integer().required().withDescription("The ID of the entity (Story, Bug, etc.) to find relations for."))
                        .build(),
                args -> search(args.path("entityId").asInt()));

        server.registerTool("relation_link", "Create a relation between two entities.",
                schema.object()
                        .prop("inboundId", schema.integer().required().withDescription("ID of the inbound entity."))
                        .prop("outboundId", schema.integer().required().withDescription("ID of the outbound entity."))
                        .prop("typeName", schema.string().withDefault("Relation").withDescription("Type of relation (e.g., 'Blocker', 'Duplicate')."))
                        .build(),
                args -> link(args.path("inboundId").asInt(), args.path("outboundId").asInt(), args.path("typeName").asText("Relation")));
    }

    private String search(int entityId) {
        List<RelationDto> relations = searchSvc.findByEntity(entityId);
        if (relations.isEmpty()) return "No relations found for entity [" + entityId + "].";
        return String.join("\n", relations.stream()
                .map(r -> "[%d] %s -> %s (%s) -> %s (%s)".formatted(
                        r.id(), r.inboundName(), r.typeName(), r.inboundType(), r.outboundName(), r.outboundType()))
                .toList());
    }

    private String link(int inboundId, int outboundId, String typeName) {
        RelationDto rel = createSvc.link(inboundId, outboundId, typeName);
        return "Relation [%d] created successfully: %s link established between %d and %d."
                .formatted(rel.id(), rel.typeName(), inboundId, outboundId);
    }
}
