package com.ibm.mcp.zdtp.iteration.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.shared.entity.NamedEntity;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IterationDto(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name,
        @JsonProperty("StartDate") OffsetDateTime startDate,
        @JsonProperty("EndDate") OffsetDateTime endDate,
        @JsonProperty("Project") NamedEntity project
) {}
