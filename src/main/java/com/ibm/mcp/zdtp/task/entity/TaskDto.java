package com.ibm.mcp.zdtp.task.entity;

public record TaskDto(
    int id,
    String name,
    String description,
    String projectName,
    String state,
    String ownerLogin,
    String createdAt,
    Integer userStoryId,
    String userStoryName
) {}
