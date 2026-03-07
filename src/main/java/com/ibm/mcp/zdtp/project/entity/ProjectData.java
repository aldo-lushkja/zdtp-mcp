package com.ibm.mcp.zdtp.project.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.shared.entity.EntityState;

public record ProjectData(
    @JsonProperty("ResourceType") String resourceType,
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name,
    @JsonProperty("Description") String description,
    @JsonProperty("EntityState") EntityState state,
    @JsonProperty("CreateDate") String createDate
) {}