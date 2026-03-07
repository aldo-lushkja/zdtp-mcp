package com.ibm.mcp.targetprocess.sprint.dto;

import lombok.Builder;

@Builder
public record SprintDto(
    int id,
    String name,
    String description,
    String projectName,
    String state,
    String ownerLogin,
    String createdAt,
    String startDate,
    String endDate
) {}