package com.ibm.mcp.zdtp.teamiteration.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.team.entity.Team;

public record TeamIteration(
    @JsonProperty("ResourceType") String resourceType,
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name,
    @JsonProperty("StartDate") String startDate,
    @JsonProperty("EndDate") String endDate,
    @JsonProperty("Team") Team team
) {}