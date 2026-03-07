package com.ibm.mcp.zdtp.testplan.boundary;

import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;
import com.ibm.mcp.zdtp.testplan.control.*;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class TestPlanMcpTools {
    private final TestPlanSearchService searchSvc;
    private final TestPlanCreateService createSvc;
    private final TestPlanUpdateService updateSvc;
    private final TestPlanGetByIdService getSvc;

    public TestPlanMcpTools(TestPlanSearchService s, TestPlanCreateService c, TestPlanUpdateService u, TestPlanGetByIdService g) {
        this.searchSvc = s; this.createSvc = c; this.updateSvc = u; this.getSvc = g;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("test_plan_search", "Search for test plans.",
                schema.object().prop("nameQuery", schema.string()).prop("projectName", schema.string()).prop("ownerLogin", schema.string())
                        .prop("startDate", schema.string()).prop("endDate", schema.string()).prop("take", schema.integer().withDefault(10)).build(),
                args -> search(new TestPlanSearchService.SearchCriteria(args.path("nameQuery").asText(null), args.path("projectName").asText(null),
                        args.path("ownerLogin").asText(null), args.path("startDate").asText(null), args.path("endDate").asText(null), args.path("take").asInt(10))));

        server.registerTool("test_plan_create", "Create a new test plan.",
                schema.object().prop("name", schema.string().required()).prop("projectId", schema.integer().required())
                        .prop("description", schema.string()).build(),
                args -> create(args.path("name").asText(), args.path("projectId").asInt(), args.path("description").asText(null)));

        server.registerTool("test_plan_update", "Update an existing test plan.",
                schema.object().prop("id", schema.integer().required()).prop("name", schema.string()).prop("description", schema.string())
                        .prop("stateName", schema.string()).build(),
                args -> update(args.path("id").asInt(), args.path("name").asText(null), args.path("description").asText(null), args.path("stateName").asText(null)));

        server.registerTool("test_plan_get", "Get a test plan by ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> get(args.path("id").asInt()));
    }

    private String search(TestPlanSearchService.SearchCriteria c) {
        var res = searchSvc.search(c);
        return res.isEmpty() ? "No test plans found." : String.join("\n", res.stream().map(this::format).toList());
    }

    private String create(String n, int p, String d) { return "Created: " + format(createSvc.create(n, p, d)); }
    private String update(int i, String n, String d, String s) { return "Updated: " + format(updateSvc.update(i, n, d, s)); }
    private String get(int i) { var t = getSvc.get(i); return format(t) + "\nDescription:\n" + (t.description() != null ? t.description() : "N/A"); }

    private String format(TestPlanDto t) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Created: %s)"
                .formatted(t.id(), t.name(), ns(t.projectName()), ns(t.state()), ns(t.ownerLogin()), ns(t.createdAt()));
    }

    private String ns(String v) { return v != null ? v : "N/A"; }
}
