package com.ibm.mcp.zdtp.testcase.boundary;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;
import com.ibm.mcp.zdtp.testcase.entity.TestStepDto;
import com.ibm.mcp.zdtp.testcase.control.*;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.ArrayList;
import java.util.List;

public class TestCaseMcpTools {
    private final TestCaseSearchService searchSvc;
    private final TestCaseCreateService createSvc;
    private final TestCaseUpdateService updateSvc;
    private final TestCaseGetByIdService getSvc;
    private final TestStepCreateService stepCreateSvc;
    private final TestCaseDeleteService deleteSvc;
    private final TestStepDeleteService stepDeleteSvc;

    public TestCaseMcpTools(TestCaseSearchService s, TestCaseCreateService c, TestCaseUpdateService u, TestCaseGetByIdService g, TestStepCreateService sc, TestCaseDeleteService ds, TestStepDeleteService sds) {
        this.searchSvc = s; this.createSvc = c; this.updateSvc = u; this.getSvc = g; this.stepCreateSvc = sc; this.deleteSvc = ds; this.stepDeleteSvc = sds;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        JsonNode stepItemSchema = schema.object()
                .prop("description", schema.string().required())
                .prop("expectedResult", schema.string())
                .prop("runOrder", schema.integer())
                .build();

        server.registerTool("test_case_search", "Search for test cases.",
                schema.object().prop("nameQuery", schema.string()).prop("projectName", schema.string()).prop("ownerLogin", schema.string())
                        .prop("startDate", schema.string()).prop("endDate", schema.string()).prop("take", schema.integer().withDefault(10)).build(),
                args -> search(new TestCaseSearchService.SearchCriteria(args.path("nameQuery").asText(null), args.path("projectName").asText(null),
                        args.path("ownerLogin").asText(null), args.path("startDate").asText(null), args.path("endDate").asText(null), args.path("take").asInt(10))));

        server.registerTool("test_case_create", "Create a new test case, optionally linked to a test plan and with inline test steps.",
                schema.object()
                        .prop("name", schema.string().required())
                        .prop("projectId", schema.integer().required())
                        .prop("description", schema.string())
                        .prop("testPlanId", schema.integer())
                        .prop("steps", schema.array().items(stepItemSchema))
                        .build(),
                args -> create(args.path("name").asText(), args.path("projectId").asInt(),
                        args.path("description").asText(null),
                        args.has("testPlanId") ? args.path("testPlanId").asInt() : null,
                        args.path("steps")));

        server.registerTool("test_case_update", "Update an existing test case.",
                schema.object().prop("id", schema.integer().required()).prop("name", schema.string()).prop("description", schema.string()).build(),
                args -> update(args.path("id").asInt(), args.path("name").asText(null), args.path("description").asText(null)));

        server.registerTool("test_case_get", "Get a test case by ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> get(args.path("id").asInt()));

        server.registerTool("test_step_create", "Add a test step to an existing test case.",
                schema.object().prop("testCaseId", schema.integer().required()).prop("description", schema.string().required())
                        .prop("expectedResult", schema.string()).prop("runOrder", schema.integer()).build(),
                args -> createStep(args.path("testCaseId").asInt(), args.path("description").asText(),
                        args.path("expectedResult").asText(null), args.has("runOrder") ? args.path("runOrder").asInt() : null));

        server.registerTool("test_case_delete", "Delete a test case by ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> delete(args.path("id").asInt()));

        server.registerTool("test_step_delete", "Delete a test step by ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> deleteStep(args.path("id").asInt()));
    }

    private String search(TestCaseSearchService.SearchCriteria c) {
        var res = searchSvc.search(c);
        return res.isEmpty() ? "No test cases found." : String.join("\n", res.stream().map(this::format).toList());
    }

    private String create(String n, int p, String d, Integer tp, JsonNode stepsNode) {
        TestCaseDto tc = createSvc.create(n, p, d, tp);
        StringBuilder sb = new StringBuilder("Created: ").append(format(tc));

        List<JsonNode> steps = new ArrayList<>();
        if (stepsNode != null && stepsNode.isArray()) {
            stepsNode.forEach(steps::add);
        }

        if (!steps.isEmpty()) {
            sb.append("\nSteps:");
            for (int i = 0; i < steps.size(); i++) {
                JsonNode s = steps.get(i);
                String desc = s.path("description").asText();
                String result = s.path("expectedResult").asText(null);
                Integer order = s.has("runOrder") ? s.path("runOrder").asInt() : null;
                TestStepDto step = stepCreateSvc.create(tc.id(), desc, result, order);
                sb.append("\n  [").append(step.id()).append("] (Order: ").append(step.runOrder()).append(") ").append(step.description());
            }
        }

        return sb.toString();
    }

    private String update(int i, String n, String d) { return "Updated: " + format(updateSvc.update(i, n, d)); }
    private String get(int i) { var t = getSvc.get(i); return format(t) + "\nDescription:\n" + (t.description() != null ? t.description() : "N/A"); }

    private String createStep(int tc, String d, String r, Integer o) {
        var s = stepCreateSvc.create(tc, d, r, o);
        return "Created Test Step [%d] (Order: %d, TestCase: %d)".formatted(s.id(), s.runOrder(), s.testCaseId());
    }

    private String delete(int i) {
        deleteSvc.delete(i);
        return "Test case [%d] deleted successfully.".formatted(i);
    }

    private String deleteStep(int i) {
        stepDeleteSvc.delete(i);
        return "Test step [%d] deleted successfully.".formatted(i);
    }

    private String format(TestCaseDto t) {
        return "[%d] %s (Project: %s, Author: %s, Created: %s, TestPlan: %s)"
                .formatted(t.id(), t.name(), ns(t.projectName()), ns(t.ownerLogin()), ns(t.createdAt()), ns(t.testPlanName()));
    }

    private String ns(String v) { return v != null ? v : "N/A"; }
}
