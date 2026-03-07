package com.ibm.mcp.targetprocess.testplan.dto;

import lombok.Builder;

@Builder
public record TestPlanDto(
    int id,
    String name,
    String description,
    String projectName,
    String state,
    String ownerLogin,
    String createdAt
) {}