package com.ibm.mcp.targetprocess.testcase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.mcp.targetprocess.shared.model.EntityState;
import com.ibm.mcp.targetprocess.shared.model.Owner;
import com.ibm.mcp.targetprocess.shared.model.Project;

import java.util.List;

public record TestCase(
    @JsonProperty("Id") Integer id,
    @JsonProperty("Name") String name,
    @JsonProperty("Description") String description,
    @JsonProperty("Project") Project project,
    @JsonProperty("EntityState") EntityState state,
    @JsonProperty("CreateDate") String createDate,
    @JsonProperty("Owner") Owner owner,
    @JsonProperty("TestPlans") TestPlanCollection testPlans
) {
    public record TestPlanRef(
        @JsonProperty("Id") Integer id,
        @JsonProperty("Name") String name
    ) {}

    public record TestPlanCollection(
        @JsonProperty("Items") List<TestPlanRef> items
    ) {}
}