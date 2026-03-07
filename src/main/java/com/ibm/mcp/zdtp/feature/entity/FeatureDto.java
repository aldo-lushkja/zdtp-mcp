package com.ibm.mcp.zdtp.feature.entity;

import lombok.Builder;

@Builder
public record FeatureDto(
    int id,
    String name,
    String description,
    String projectName,
    String state,
    String ownerLogin,
    Double effort,
    String createdAt,
    String endDate,
    Integer sprintId,
    String sprintName
) {}
