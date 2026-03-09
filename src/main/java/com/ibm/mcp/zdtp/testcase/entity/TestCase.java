package com.ibm.mcp.zdtp.testcase.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.zdtp.shared.entity.Owner;
import com.ibm.mcp.zdtp.shared.entity.Project;

import java.util.List;

public record TestCase(
    @JsonProperty("ResourceType") String resourceType,
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name,
    @JsonProperty("Description") String description,
    @JsonProperty("Project") Project project,
    @JsonProperty("CreateDate") String createDate,
    @JsonProperty("Owner") Owner owner,
    @JsonProperty("TestPlans") TestPlanCollection testPlans
) {
    public record TestPlanRef(
        @JsonProperty("ResourceType") String resourceType,
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name
    ) {}

    public record TestPlanCollection(
        @JsonProperty("ResourceType") String resourceType,
        @JsonProperty("Items") List<TestPlanRef> items
    ) {}
}