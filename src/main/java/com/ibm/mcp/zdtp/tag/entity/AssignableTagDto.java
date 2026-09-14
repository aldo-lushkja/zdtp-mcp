package com.ibm.mcp.zdtp.tag.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AssignableTagDto(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Tags") String tags
) {}
