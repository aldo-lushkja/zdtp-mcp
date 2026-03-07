package com.ibm.mcp.zdtp.shared.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record Project(@JsonProperty("Id") Integer id, @JsonProperty("Name") String name) {}
