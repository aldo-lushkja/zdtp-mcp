package com.ibm.mcp.zdtp.teamiteration.entity;

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