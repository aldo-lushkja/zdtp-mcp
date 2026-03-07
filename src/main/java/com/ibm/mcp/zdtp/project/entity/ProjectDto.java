package com.ibm.mcp.zdtp.project.entity;

public record ProjectDto(
    int id,
    String name,
    String description,
    String state,
    String createdAt
) {}