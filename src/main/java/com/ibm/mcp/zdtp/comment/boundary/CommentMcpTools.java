package com.ibm.mcp.zdtp.comment.boundary;

import com.ibm.mcp.zdtp.comment.control.CommentCreateService;
import com.ibm.mcp.zdtp.comment.entity.CommentDto;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class CommentMcpTools {
    private final CommentCreateService createService;

    public CommentMcpTools(CommentCreateService createService) {
        this.createService = createService;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("comment_add", "Add a comment to any Targetprocess entity (User Story, Task, Bug, Test Case, etc.).",
                schema.object()
                        .prop("entityId", schema.integer().required().withDescription("The numeric ID of the entity to comment on."))
                        .prop("text", schema.string().required().withDescription("The text of the comment. Supports Markdown."))
                        .build(),
                args -> addComment(args.path("entityId").asInt(), args.path("text").asText()));
    }

    private String addComment(int entityId, String text) {
        CommentDto comment = createService.addComment(entityId, text);
        return "Comment [%d] added to entity [%d] (%s) by %s."
                .formatted(comment.id(), comment.entityId(), comment.entityName(), comment.author());
    }
}
