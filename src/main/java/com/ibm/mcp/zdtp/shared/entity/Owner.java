package com.ibm.mcp.zdtp.shared.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record Owner(@JsonProperty("Id") Integer id, @JsonProperty("Login") String login) {}
