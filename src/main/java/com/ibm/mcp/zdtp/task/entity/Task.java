package com.ibm.mcp.zdtp.task.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.shared.entity.EntityState;
import com.ibm.mcp.zdtp.shared.entity.Owner;
import com.ibm.mcp.zdtp.shared.entity.Project;

public record Task(
    @JsonProperty("ResourceType") String resourceType,
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name,
    @JsonProperty("Description") String description,
    @JsonProperty("Project") Project project,
    @JsonProperty("EntityState") EntityState state,
    @JsonProperty("CreateDate") String createDate,
    @JsonProperty("Owner") Owner owner,
    @JsonProperty("UserStory") UserStoryRef userStory
) {
    public record UserStoryRef(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name
    ) {}
}
