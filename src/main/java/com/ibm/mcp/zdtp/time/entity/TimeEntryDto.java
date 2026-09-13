package com.ibm.mcp.zdtp.time.entity;

public record TimeEntryDto(
    int id,
    double spent,
    String description,
    String date,
    String userLogin
) {}
