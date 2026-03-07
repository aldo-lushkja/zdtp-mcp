package com.ibm.mcp.targetprocess.team.dto;

import lombok.Builder;

@Builder
public record TeamDto(
    int id,
    String name
) {}