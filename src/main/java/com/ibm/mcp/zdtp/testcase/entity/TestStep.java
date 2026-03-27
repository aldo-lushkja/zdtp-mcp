package com.ibm.mcp.zdtp.testcase.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TestStep(
    @JsonProperty("ResourceType") String resourceType,
    @JsonProperty("Id") Integer id,
    @JsonProperty("Description") String description,
    @JsonProperty("Result") String result,
    @JsonProperty("RunOrder") Integer runOrder,
    @JsonProperty("TestCase") TestCaseRef testCase
) {
    public record TestCaseRef(
        @JsonProperty("ResourceType") String resourceType,
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name
    ) {}
}
