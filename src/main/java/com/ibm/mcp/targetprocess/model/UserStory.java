package com.ibm.mcp.targetprocess.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record UserStory(
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name,
    @JsonProperty("Description") String description,
    @JsonProperty("Project") Project project,
    @JsonProperty("EntityState") EntityState state
) {
    public record Project(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name
    ) {}

    public record EntityState(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name
    ) {}
}
