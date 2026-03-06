package com.ibm.mcp.targetprocess.dto;

import lombok.Builder;

@Builder
public record UserStoryDto(
    int id,
    String name,
    String description,
    String projectName,
    String state,
    String ownerLogin,
    String createdAt
) {}
