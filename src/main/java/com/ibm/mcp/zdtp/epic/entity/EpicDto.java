package com.ibm.mcp.zdtp.epic.entity;

public record EpicDto(
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