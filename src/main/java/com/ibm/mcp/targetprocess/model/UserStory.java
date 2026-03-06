package com.ibm.mcp.targetprocess.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserStory(
    @JsonProperty("id") Integer id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("project") Project project,
    @JsonProperty("entityState") EntityState state,
    @JsonProperty("createDate") String createDate,
    @JsonProperty("owner") Owner owner
) {}
