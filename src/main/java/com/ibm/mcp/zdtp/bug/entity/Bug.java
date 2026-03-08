package com.ibm.mcp.zdtp.bug.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.shared.entity.EntityState;
import com.ibm.mcp.zdtp.shared.entity.Owner;
import com.ibm.mcp.zdtp.shared.entity.Project;
import com.ibm.mcp.zdtp.shared.entity.ReleaseReference;
import com.ibm.mcp.zdtp.shared.entity.SprintReference;

public record Bug(
    @JsonProperty("ResourceType") String resourceType,
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name,
    @JsonProperty("Description") String description,
    @JsonProperty("Project") Project project,
    @JsonProperty("EntityState") EntityState state,
    @JsonProperty("CreateDate") String createDate,
    @JsonProperty("EndDate") String endDate,
    @JsonProperty("Effort") Double effort,
    @JsonProperty("Owner") Owner owner,
    @JsonProperty("AssignedUser") AssignedUserCollection assignedUser,
    @JsonProperty("Release") ReleaseReference release,
    @JsonProperty("UserStory") UserStoryRef userStory,
    @JsonProperty("Feature") FeatureRef feature,
    @JsonProperty("TeamIteration") SprintReference sprint
) {
    public record AssignedUserCollection(
        @JsonProperty("Items") java.util.List<Owner> items
    ) {}
    
    public record UserStoryRef(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name
    ) {}
    
    public record FeatureRef(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name
    ) {}
}
