package com.ibm.mcp.zdtp.testplan.entity;

public record TestPlanDto(
    int id,
    String name,
    String description,
    String projectName,
    String state,
    Integer stateId,
    String ownerLogin,
    String createdAt
) {}