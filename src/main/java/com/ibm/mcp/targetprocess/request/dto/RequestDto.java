package com.ibm.mcp.targetprocess.request.dto;

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
