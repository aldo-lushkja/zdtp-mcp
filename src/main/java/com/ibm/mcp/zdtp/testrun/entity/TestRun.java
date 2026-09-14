package com.ibm.mcp.zdtp.testrun.entity;

import java.time.OffsetDateTime;

public record TestRun(
        Integer id,
        String name,
        OffsetDateTime createDate,
        Integer testPlanId,
        String testPlanName,
        Integer projectId,
        String projectName
) {}
