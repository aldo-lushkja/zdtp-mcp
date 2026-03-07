package com.ibm.mcp.targetprocess.epic.dto;

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