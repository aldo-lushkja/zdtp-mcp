package com.ibm.mcp.zdtp.assignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.shared.entity.NamedEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AssignmentDto(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Role") NamedEntity role,
        @JsonProperty("General") NamedEntity general,
        @JsonProperty("GeneralUser") UserLoginDto generalUser
) {}
