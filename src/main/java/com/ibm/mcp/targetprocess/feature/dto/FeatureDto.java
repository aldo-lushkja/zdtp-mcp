package com.ibm.mcp.targetprocess.feature.dto;

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
    String endDate
) {}
