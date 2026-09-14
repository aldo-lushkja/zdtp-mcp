package com.ibm.mcp.zdtp.customfield.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomFieldDto(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name,
        @JsonProperty("FieldType") String fieldType,
        @JsonProperty("EntityKind") String entityKind,
        @JsonProperty("Value") Object value
) {}
