package com.ibm.mcp.zdtp.impediment.entity;

public record ImpedimentDto(
    int id,
    String name,
    String description,
    String state
) {}
