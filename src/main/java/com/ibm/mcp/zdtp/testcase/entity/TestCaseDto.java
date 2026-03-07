package com.ibm.mcp.zdtp.testcase.entity;

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