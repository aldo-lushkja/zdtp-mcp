package com.ibm.mcp.zdtp.impediment.boundary;

import com.ibm.mcp.zdtp.impediment.control.ImpedimentCreateService;
import com.ibm.mcp.zdtp.impediment.control.ImpedimentSearchService;
import com.ibm.mcp.zdtp.impediment.entity.ImpedimentDto;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class ImpedimentMcpTools {
    private final ImpedimentCreateService createSvc;
    private final ImpedimentSearchService searchSvc;

    public ImpedimentMcpTools(ImpedimentCreateService createSvc, ImpedimentSearchService searchSvc) {
        this.createSvc = createSvc;
        this.searchSvc = searchSvc;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ImpedimentCreateService createSvc;
        private ImpedimentSearchService searchSvc;

        public Builder createSvc(ImpedimentCreateService createSvc) { this.createSvc = createSvc; return this; }
        public Builder searchSvc(ImpedimentSearchService searchSvc) { this.searchSvc = searchSvc; return this; }

        public ImpedimentMcpTools build() { return new ImpedimentMcpTools(createSvc, searchSvc); }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("impediment_create", "Raise an impediment / blocker on a User Story, Task, or Bug.",
                schema.object()
                        .prop("entityId", schema.integer().required().withDescription("ID of the blocked entity."))
                        .prop("name", schema.string().required().withDescription("Short summary of the blocker."))
                        .prop("description", schema.string().withDescription("Detailed description of the impediment."))
                        .build(),
                args -> create(args.path("entityId").asInt(), args.path("name").asText(), args.path("description").asText(null)));

        server.registerTool("impediment_search", "List impediments / blockers attached to an entity.",
                schema.object()
                        .prop("entityId", schema.integer().required().withDescription("ID of the entity to search impediments for."))
                        .build(),
                args -> search(args.path("entityId").asInt()));
    }

    private String create(int entityId, String name, String description) {
        ImpedimentDto imp = createSvc.create(entityId, name, description);
        return "Impediment [%d] raised on entity [%d]: %s".formatted(imp.id(), entityId, imp.name());
    }

    private String search(int entityId) {
        List<ImpedimentDto> res = searchSvc.search(entityId);
        if (res.isEmpty()) return "No impediments found for entity [%d].".formatted(entityId);
        return String.join("\n", res.stream().map(i -> "[%d] %s (State: %s)".formatted(i.id(), i.name(), i.state() != null ? i.state() : "N/A")).toList());
    }
}
