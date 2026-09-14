package com.ibm.mcp.zdtp.shared.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NamedEntity(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name
) {}
