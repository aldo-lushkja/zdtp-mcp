package com.ibm.mcp.targetprocess.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record Project(@JsonProperty("id") Integer id, @JsonProperty("name") String name) {}
