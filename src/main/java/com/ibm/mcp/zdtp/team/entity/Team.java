package com.ibm.mcp.zdtp.team.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Team(
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name
) {}