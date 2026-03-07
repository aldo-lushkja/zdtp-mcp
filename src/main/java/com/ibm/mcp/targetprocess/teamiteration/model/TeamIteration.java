package com.ibm.mcp.targetprocess.teamiteration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.targetprocess.team.model.Team;

public record TeamIteration(
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name,
    @JsonProperty("StartDate") String startDate,
    @JsonProperty("EndDate") String endDate,
    @JsonProperty("Team") Team team
) {}