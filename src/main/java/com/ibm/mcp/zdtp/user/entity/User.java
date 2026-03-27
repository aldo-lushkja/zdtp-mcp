package com.ibm.mcp.zdtp.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record User(
    @JsonProperty("ResourceType") String resourceType,
    @JsonProperty("Id") Integer id,
    @JsonProperty("FirstName") String firstName,
    @JsonProperty("LastName") String lastName,
    @JsonProperty("Login") String login,
    @JsonProperty("Email") String email,
    @JsonProperty("IsActive") Boolean isActive
) {}
