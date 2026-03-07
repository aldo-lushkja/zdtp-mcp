package com.ibm.mcp.zdtp.epic.entity;

import lombok.Builder;

@Builder
public record EpicDto(
    int id,
    String name,
    String description,
    String projectName,
    String state,
    String ownerLogin,
    Double effort,
    String createdAt,
    String endDate
) {}