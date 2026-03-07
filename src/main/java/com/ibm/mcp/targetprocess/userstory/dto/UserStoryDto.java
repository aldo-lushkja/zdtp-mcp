package com.ibm.mcp.targetprocess.userstory.dto;

import lombok.Builder;

@Builder
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
    String sprintName
) {}
