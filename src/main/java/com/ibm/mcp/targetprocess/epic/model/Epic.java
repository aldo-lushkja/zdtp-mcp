package com.ibm.mcp.targetprocess.epic.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.targetprocess.shared.model.EntityState;
import com.ibm.mcp.targetprocess.shared.model.Owner;
import com.ibm.mcp.targetprocess.shared.model.Project;

public record Epic(
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name,
    @JsonProperty("Description") String description,
    @JsonProperty("Project") Project project,
    @JsonProperty("EntityState") EntityState state,
    @JsonProperty("CreateDate") String createDate,
    @JsonProperty("EndDate") String endDate,
    @JsonProperty("Effort") Double effort,
    @JsonProperty("Owner") Owner owner
) {}