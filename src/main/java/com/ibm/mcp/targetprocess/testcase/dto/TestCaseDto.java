package com.ibm.mcp.targetprocess.testcase.dto;

import lombok.Builder;

@Builder
public record TestCaseDto(
    int id,
    String name,
    String description,
    String projectName,
    String state,
    String ownerLogin,
    String createdAt,
    String testPlanName
) {}