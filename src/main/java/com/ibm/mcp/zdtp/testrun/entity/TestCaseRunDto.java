package com.ibm.mcp.zdtp.testrun.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.shared.entity.NamedEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TestCaseRunDto(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Status") String status,
        @JsonProperty("Comment") String comment,
        @JsonProperty("TestCase") NamedEntity testCase,
        @JsonProperty("TestPlanRun") NamedEntity testPlanRun
) {}
