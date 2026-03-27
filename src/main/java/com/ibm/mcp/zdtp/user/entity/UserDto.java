package com.ibm.mcp.zdtp.user.entity;

public record UserDto(
    int id,
    String firstName,
    String lastName,
    String login,
    String email,
    boolean isActive
) {}
