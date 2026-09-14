package com.ibm.mcp.zdtp.testrun.entity;

public record TestCaseRun(
        Integer id,
        String status,
        String comment,
        Integer testCaseId,
        String testCaseName,
        Integer testPlanRunId,
        String testPlanRunName
) {}
