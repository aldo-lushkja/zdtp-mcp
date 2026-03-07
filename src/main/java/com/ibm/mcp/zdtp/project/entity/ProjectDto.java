package com.ibm.mcp.zdtp.project.entity;

import lombok.Builder;

@Builder
public record ProjectDto(
    int id,
    String name,
    String description,
    String state,
    String createdAt
) {}