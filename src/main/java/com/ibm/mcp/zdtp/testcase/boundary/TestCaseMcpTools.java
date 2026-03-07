package com.ibm.mcp.zdtp.testcase.boundary;

import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;
import com.ibm.mcp.zdtp.testcase.control.*;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class TestCaseMcpTools {
    private final TestCaseSearchService searchSvc;
    private final TestCaseCreateService createSvc;
    private final TestCaseUpdateService updateSvc;
    private final TestCaseGetByIdService getSvc;

    public TestCaseMcpTools(TestCaseSearchService s, TestCaseCreateService c, TestCaseUpdateService u, TestCaseGetByIdService g) {
        this.searchSvc = s; this.createSvc = c; this.updateSvc = u; this.getSvc = g;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("test_case_search", "Search for test cases.",
                schema.object().prop("nameQuery", schema.string()).prop("projectName", schema.string()).prop("ownerLogin", schema.string())
                        .prop("startDate", schema.string()).prop("endDate", schema.string()).prop("take", schema.integer().withDefault(10)).build(),
                args -> search(new TestCaseSearchService.SearchCriteria(args.path("nameQuery").asText(null), args.path("projectName").asText(null),
                        args.path("ownerLogin").asText(null), args.path("startDate").asText(null), args.path("endDate").asText(null), args.path("take").asInt(10))));

        server.registerTool("test_case_create", "Create a new test case.",
                schema.object().prop("name", schema.string().required()).prop("projectId", schema.integer().required())
                        .prop("description", schema.string()).prop("testPlanId", schema.integer()).build(),
                args -> create(args.path("name").asText(), args.path("projectId").asInt(), args.path("description").asText(null), args.has("testPlanId") ? args.path("testPlanId").asInt() : null));

        server.registerTool("test_case_update", "Update an existing test case.",
                schema.object().prop("id", schema.integer().required()).prop("name", schema.string()).prop("description", schema.string())
                        .prop("stateName", schema.string()).build(),
                args -> update(args.path("id").asInt(), args.path("name").asText(null), args.path("description").asText(null), args.path("stateName").asText(null)));

        server.registerTool("test_case_get", "Get a test case by ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> get(args.path("id").asInt()));
    }

    private String search(TestCaseSearchService.SearchCriteria c) {
        var res = searchSvc.search(c);
        return res.isEmpty() ? "No test cases found." : String.join("\n", res.stream().map(this::format).toList());
    }

    private String create(String n, int p, String d, Integer tp) { return "Created: " + format(createSvc.create(n, p, d, tp)); }
    private String update(int i, String n, String d, String s) { return "Updated: " + format(updateSvc.update(i, n, d, s)); }
    private String get(int i) { var t = getSvc.get(i); return format(t) + "\nDescription:\n" + (t.description() != null ? t.description() : "N/A"); }

    private String format(TestCaseDto t) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Created: %s, TestPlan: %s)"
                .formatted(t.id(), t.name(), ns(t.projectName()), ns(t.state()), ns(t.ownerLogin()), ns(t.createdAt()), ns(t.testPlanName()));
    }

    private String ns(String v) { return v != null ? v : "N/A"; }
}
