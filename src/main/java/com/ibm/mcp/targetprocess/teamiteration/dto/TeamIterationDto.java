package com.ibm.mcp.targetprocess.teamiteration.dto;

import lombok.Builder;

@Builder
public record TeamIterationDto(
    int id,
    String name,
    String startDate,
    String endDate,
    Integer teamId,
    String teamName
) {}