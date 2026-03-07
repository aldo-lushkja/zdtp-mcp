package com.ibm.mcp.targetprocess.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.targetprocess.shared.model.EntityState;

public record ProjectData(
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name,
    @JsonProperty("Description") String description,
    @JsonProperty("EntityState") EntityState state,
    @JsonProperty("CreateDate") String createDate
) {}