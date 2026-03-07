package com.ibm.mcp.targetprocess.team.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Team(
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name
) {}