package com.ibm.mcp.zdtp.assignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserLoginDto(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Login") String login
) {}
