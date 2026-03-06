package com.ibm.mcp.targetprocess.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record EntityState(@JsonProperty("Id") Integer id, @JsonProperty("Name") String name) {}
