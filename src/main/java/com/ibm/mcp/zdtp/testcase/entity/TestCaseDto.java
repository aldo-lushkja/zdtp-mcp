package com.ibm.mcp.zdtp.testcase.entity;

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