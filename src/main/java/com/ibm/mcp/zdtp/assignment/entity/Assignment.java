package com.ibm.mcp.zdtp.assignment.entity;

public record Assignment(
        Integer id,
        Integer roleId,
        String roleName,
        Integer entityId,
        String entityName,
        Integer userId,
        String userLogin
) {}
