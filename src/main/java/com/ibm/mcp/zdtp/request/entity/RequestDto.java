package com.ibm.mcp.zdtp.request.entity;

import lombok.Builder;

@Builder
public record RequestDto(
    int id,
    String name,
    String description,
    String projectName,
    String state,
    String ownerLogin,
    Double effort,
    String createdAt,
    String endDate
) {}
