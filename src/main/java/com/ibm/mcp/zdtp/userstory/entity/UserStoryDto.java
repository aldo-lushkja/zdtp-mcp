package com.ibm.mcp.zdtp.userstory.entity;

public record UserStoryDto(
    int id,
    String name,
    String description,
    String projectName,
    String state,
    String ownerLogin,
    String assigneeLogin,
    Double effort,
    String createdAt,
    String endDate,
    Integer releaseId,
    String releaseName,
    Integer sprintId,
    String sprintName,
    String teamName
) {}
