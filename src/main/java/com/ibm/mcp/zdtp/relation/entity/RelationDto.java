package com.ibm.mcp.zdtp.relation.entity;

public record RelationDto(
    int id,
    String typeName,
    int inboundId,
    String inboundName,
    String inboundType,
    int outboundId,
    String outboundName,
    String outboundType
) {}
