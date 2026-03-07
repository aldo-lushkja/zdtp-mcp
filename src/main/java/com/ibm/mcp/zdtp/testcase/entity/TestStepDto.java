package com.ibm.mcp.zdtp.testcase.entity;

public record TestStepDto(
    int id,
    String description,
    String expectedResult,
    int runOrder,
    int testCaseId
) {}
