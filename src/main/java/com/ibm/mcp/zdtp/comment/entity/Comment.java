package com.ibm.mcp.zdtp.comment.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.shared.entity.Owner;

public record Comment(
    @JsonProperty("ResourceType") String resourceType,
    @JsonProperty("Id") Integer id,
    @JsonProperty("Description") String description,
    @JsonProperty("CreateDate") String createDate,
    @JsonProperty("Owner") Owner owner,
    @JsonProperty("General") GeneralRef general
) {
    public record GeneralRef(
        @JsonProperty("ResourceType") String resourceType,
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name
    ) {}
}
