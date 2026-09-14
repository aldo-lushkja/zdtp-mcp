package com.ibm.mcp.zdtp.testrun.boundary;

import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;
import com.ibm.mcp.zdtp.testrun.control.TestCaseRunUpdateService;
import com.ibm.mcp.zdtp.testrun.control.TestRunCreateService;
import com.ibm.mcp.zdtp.testrun.control.TestRunSearchService;
import com.ibm.mcp.zdtp.testrun.entity.TestCaseRun;
import com.ibm.mcp.zdtp.testrun.entity.TestRun;

import java.util.List;

public class TestRunMcpTools {
    private final TestRunCreateService createService;
    private final TestRunSearchService searchService;
    private final TestCaseRunUpdateService updateCaseRunService;

    public TestRunMcpTools(TestRunCreateService createService, TestRunSearchService searchService, TestCaseRunUpdateService updateCaseRunService) {
        this.createService = createService;
        this.searchService = searchService;
        this.updateCaseRunService = updateCaseRunService;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private TestRunCreateService createService;
        private TestRunSearchService searchService;
        private TestCaseRunUpdateService updateCaseRunService;

        public Builder createService(TestRunCreateService createService) { this.createService = createService; return this; }
        public Builder searchService(TestRunSearchService searchService) { this.searchService = searchService; return this; }
        public Builder updateCaseRunService(TestCaseRunUpdateService updateCaseRunService) { this.updateCaseRunService = updateCaseRunService; return this; }
        public TestRunMcpTools build() { return new TestRunMcpTools(createService, searchService, updateCaseRunService); }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("test_run_create", "Create a new test run for a test plan.",
                schema.object()
                        .prop("name", schema.string().required().withDescription("Name of the test run."))
                        .prop("projectId", schema.integer().required().withDescription("Project ID."))
                        .prop("testPlanId", schema.integer().required().withDescription("Test Plan ID."))
                        .prop("description", schema.string().withDescription("Test run description."))
                        .build(),
                args -> {
                    TestRun tr = createService.create(
                            args.path("name").asText(),
                            args.path("projectId").asInt(),
                            args.path("testPlanId").asInt(),
                            args.path("description").asText(null)
                    );
                    return "Created Test Run [%d]: %s".formatted(tr.id(), tr.name());
                });

        server.registerTool("test_run_search", "Search for test runs.",
                schema.object()
                        .prop("nameQuery", schema.string().withDescription("Filter by name."))
                        .prop("projectId", schema.integer().withDescription("Filter by project ID."))
                        .prop("testPlanId", schema.integer().withDescription("Filter by test plan ID."))
                        .prop("take", schema.integer().withDescription("Max results (default: 10)."))
                        .prop("skip", schema.integer().withDescription("Number of items to skip for pagination."))
                        .build(),
                args -> {
                    List<TestRun> list = searchService.search(
                            args.path("nameQuery").asText(null),
                            args.has("projectId") ? args.path("projectId").asInt() : null,
                            args.has("testPlanId") ? args.path("testPlanId").asInt() : null,
                            args.has("take") ? args.path("take").asInt() : 10,
                            args.has("skip") ? args.path("skip").asInt() : null
                    );
                    if (list.isEmpty()) return "No test runs found.";
                    return String.join("\n", list.stream().map(r -> "[%d] %s".formatted(r.id(), r.name())).toList());
                });

        server.registerTool("test_case_run_update", "Update status or comment of a test case run.",
                schema.object()
                        .prop("id", schema.integer().required().withDescription("Test case run ID."))
                        .prop("status", schema.string().required().withDescription("Status (Passed, Failed, Blocked, OnHold)."))
                        .prop("comment", schema.string().withDescription("Execution comment."))
                        .build(),
                args -> {
                    TestCaseRun run = updateCaseRunService.update(
                            args.path("id").asInt(),
                            args.path("status").asText(),
                            args.path("comment").asText(null)
                    );
                    return "TestCaseRun [%d] updated status to: %s".formatted(run.id(), run.status());
                });
    }
}
