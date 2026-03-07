package com.ibm.mcp.zdtp.team.entity;

import lombok.Builder;

@Builder
public record TeamDto(
    int id,
    String name
) {}