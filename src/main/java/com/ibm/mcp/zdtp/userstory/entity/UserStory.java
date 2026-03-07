package com.ibm.mcp.zdtp.userstory.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.shared.entity.EntityState;
import com.ibm.mcp.zdtp.shared.entity.Owner;
import com.ibm.mcp.zdtp.shared.entity.Project;
import com.ibm.mcp.zdtp.shared.entity.ReleaseReference;
import com.ibm.mcp.zdtp.shared.entity.SprintReference;

public record UserStory(
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name,
    @JsonProperty("Description") String description,
    @JsonProperty("Project") Project project,
    @JsonProperty("EntityState") EntityState state,
    @JsonProperty("CreateDate") String createDate,
    @JsonProperty("EndDate") String endDate,
    @JsonProperty("Effort") Double effort,
    @JsonProperty("Owner") Owner owner,
    @JsonProperty("AssignedUser") Owner assignedUser,
    @JsonProperty("Release") ReleaseReference release,
    @JsonProperty("TeamIteration") SprintReference sprint
) {}
