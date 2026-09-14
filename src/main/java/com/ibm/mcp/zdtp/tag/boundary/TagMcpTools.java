package com.ibm.mcp.zdtp.tag.boundary;

import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;
import com.ibm.mcp.zdtp.tag.control.TagAddService;
import com.ibm.mcp.zdtp.tag.control.TagRemoveService;
import com.ibm.mcp.zdtp.tag.entity.TagResult;

public class TagMcpTools {
    private final TagAddService addService;
    private final TagRemoveService removeService;

    public TagMcpTools(TagAddService addService, TagRemoveService removeService) {
        this.addService = addService;
        this.removeService = removeService;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private TagAddService addService;
        private TagRemoveService removeService;

        public Builder addService(TagAddService addService) { this.addService = addService; return this; }
        public Builder removeService(TagRemoveService removeService) { this.removeService = removeService; return this; }
        public TagMcpTools build() { return new TagMcpTools(addService, removeService); }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("tag_add", "Add a tag to an entity.",
                schema.object()
                        .prop("entityId", schema.integer().required().withDescription("ID of the entity (UserStory, Bug, Task, Feature, Epic, Request)."))
                        .prop("tag", schema.string().required().withDescription("Tag name to add."))
                        .build(),
                args -> {
                    TagResult res = addService.addTag(args.path("entityId").asInt(), args.path("tag").asText());
                    return "Entity [%d] tags updated: %s".formatted(res.entityId(), res.tags());
                });

        server.registerTool("tag_remove", "Remove a tag from an entity.",
                schema.object()
                        .prop("entityId", schema.integer().required().withDescription("ID of the entity."))
                        .prop("tag", schema.string().required().withDescription("Tag name to remove."))
                        .build(),
                args -> {
                    TagResult res = removeService.removeTag(args.path("entityId").asInt(), args.path("tag").asText());
                    return "Entity [%d] tags updated: %s".formatted(res.entityId(), res.tags());
                });
    }
}
