package com.ibm.mcp.zdtp.customfield.entity;

public record CustomFieldInfo(
        Integer id,
        String name,
        String fieldType,
        String entityKind,
        Object value
) {}
