package com.ibm.mcp.zdtp.teamiteration.entity;

public record TeamIterationDto(
    int id,
    String name,
    String startDate,
    String endDate,
    Integer teamId,
    String teamName
) {}