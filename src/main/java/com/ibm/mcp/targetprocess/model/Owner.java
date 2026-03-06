package com.ibm.mcp.targetprocess.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record Owner(@JsonProperty("id") Integer id, @JsonProperty("login") String login) {}
