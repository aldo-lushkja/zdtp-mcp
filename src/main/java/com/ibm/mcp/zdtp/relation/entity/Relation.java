package com.ibm.mcp.zdtp.relation.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Relation(
    @JsonProperty("ResourceType") String resourceType,
    @JsonProperty("Id") Integer id,
    @JsonProperty("RelationType") RelationTypeRef relationType,
    @JsonProperty("Master") GeneralRef master,
    @JsonProperty("Slave") GeneralRef slave
) {
    public record RelationTypeRef(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name
    ) {}

    public record GeneralRef(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name,
        @JsonProperty("ResourceType") String resourceType
    ) {}
}
