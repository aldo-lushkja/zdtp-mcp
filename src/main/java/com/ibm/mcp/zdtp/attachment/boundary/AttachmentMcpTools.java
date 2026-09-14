package com.ibm.mcp.zdtp.attachment.boundary;

import com.ibm.mcp.zdtp.attachment.control.AttachmentSearchService;
import com.ibm.mcp.zdtp.attachment.entity.Attachment;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class AttachmentMcpTools {
    private final AttachmentSearchService searchService;

    public AttachmentMcpTools(AttachmentSearchService searchService) {
        this.searchService = searchService;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private AttachmentSearchService searchService;

        public Builder searchService(AttachmentSearchService searchService) { this.searchService = searchService; return this; }
        public AttachmentMcpTools build() { return new AttachmentMcpTools(searchService); }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("attachment_search", "List attachments linked to an entity.",
                schema.object()
                        .prop("entityId", schema.integer().required().withDescription("ID of the entity to list attachments for."))
                        .prop("take", schema.integer().withDescription("Max results (default: 10)."))
                        .build(),
                args -> {
                    List<Attachment> list = searchService.search(
                            args.path("entityId").asInt(),
                            args.has("take") ? args.path("take").asInt() : 10
                    );
                    if (list.isEmpty()) return "No attachments found for entity [%d].".formatted(args.path("entityId").asInt());
                    return String.join("\n", list.stream().map(att -> "[%d] %s (Owner: %s)".formatted(att.id(), att.name(), att.ownerLogin() != null ? att.ownerLogin() : "N/A")).toList());
                });
    }
}
