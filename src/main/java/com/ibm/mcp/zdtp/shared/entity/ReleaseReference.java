package com.ibm.mcp.zdtp.shared.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReleaseReference(
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name
) {}
