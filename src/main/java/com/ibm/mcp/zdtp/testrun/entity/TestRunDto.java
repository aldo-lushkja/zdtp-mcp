package com.ibm.mcp.zdtp.testrun.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.shared.entity.NamedEntity;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TestRunDto(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name,
        @JsonProperty("CreateDate") OffsetDateTime createDate,
        @JsonProperty("TestPlan") NamedEntity testPlan,
        @JsonProperty("Project") NamedEntity project
) {}
