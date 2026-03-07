package com.ibm.mcp.targetprocess.project.dto;

import lombok.Builder;

@Builder
public record ProjectDto(
    int id,
    String name,
    String description,
    String state,
    String createdAt
) {}