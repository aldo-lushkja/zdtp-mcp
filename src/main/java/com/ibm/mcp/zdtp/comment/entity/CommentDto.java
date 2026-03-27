package com.ibm.mcp.zdtp.comment.entity;

public record CommentDto(
    int id,
    String text,
    String author,
    String createdAt,
    int entityId,
    String entityName
) {}
