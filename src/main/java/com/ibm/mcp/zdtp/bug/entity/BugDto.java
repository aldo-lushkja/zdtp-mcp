package com.ibm.mcp.zdtp.bug.entity;

public record BugDto(
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
    Integer userStoryId,
    String userStoryName,
    Integer featureId,
    String featureName,
    Integer sprintId,
    String sprintName
) {}
