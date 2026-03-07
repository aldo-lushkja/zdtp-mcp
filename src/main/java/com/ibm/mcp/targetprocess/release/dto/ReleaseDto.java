package com.ibm.mcp.targetprocess.release.dto;

import lombok.Builder;

@Builder
public record ReleaseDto(
    int id,
    String name,
    String description,
    String projectName,
    String state,
    String ownerLogin,
    Double effort,
    String createdAt,
    String startDate,
    String endDate
) {}
