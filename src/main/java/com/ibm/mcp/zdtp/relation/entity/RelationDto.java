package com.ibm.mcp.zdtp.relation.entity;

public record RelationDto(
    int id,
    String typeName,
    int masterId,
    String masterName,
    String masterType,
    int slaveId,
    String slaveName,
    String slaveType
) {}
