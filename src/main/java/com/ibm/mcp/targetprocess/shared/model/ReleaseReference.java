package com.ibm.mcp.targetprocess.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReleaseReference(
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name
) {}
