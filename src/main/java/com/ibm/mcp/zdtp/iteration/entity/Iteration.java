package com.ibm.mcp.zdtp.iteration.entity;

import java.time.OffsetDateTime;

public record Iteration(
        Integer id,
        String name,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        Integer projectId,
        String projectName
) {}
