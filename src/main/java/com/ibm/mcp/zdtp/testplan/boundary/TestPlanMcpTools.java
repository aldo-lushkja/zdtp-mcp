package com.ibm.mcp.zdtp.testplan.boundary;

import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;
import com.ibm.mcp.zdtp.testplan.control.TestPlanCreateService;
import com.ibm.mcp.zdtp.testplan.control.TestPlanGetByIdService;
import com.ibm.mcp.zdtp.testplan.control.TestPlanSearchService;
import com.ibm.mcp.zdtp.testplan.control.TestPlanUpdateService;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class TestPlanMcpTools {

    private final TestPlanSearchService testPlanSearchService;
    private final TestPlanCreateService testPlanCreateService;
    private final TestPlanUpdateService testPlanUpdateService;
    private final TestPlanGetByIdService testPlanGetByIdService;

    public TestPlanMcpTools(TestPlanSearchService testPlanSearchService,
                            TestPlanCreateService testPlanCreateService,
                            TestPlanUpdateService testPlanUpdateService,
                            TestPlanGetByIdService testPlanGetByIdService) {
        this.testPlanSearchService = testPlanSearchService;
        this.testPlanCreateService = testPlanCreateService;
        this.testPlanUpdateService = testPlanUpdateService;
        this.testPlanGetByIdService = testPlanGetByIdService;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("test_plan_search", 
            "Search for test plans in Targetprocess. Supports filtering by test plan name, project name, owner login, and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.",
            schema.object()
                .prop("nameQuery", schema.string())
                .prop("projectName", schema.string())
                .prop("ownerLogin", schema.string())
                .prop("startDate", schema.string())
                .prop("endDate", schema.string())
                .prop("take", schema.integer().withDefault(10))
                .build(),
            args -> searchTestPlans(
                args.path("nameQuery").asText(null),
                args.path("projectName").asText(null),
                args.path("ownerLogin").asText(null),
                args.path("startDate").asText(null),
                args.path("endDate").asText(null),
                args.path("take").asInt(10)
            )
        );

        server.registerTool("test_plan_create",
            "Create a new test plan in Targetprocess. Requires name and projectId (numeric ID of the project). Description is optional. IMPORTANT — description formatting rules: (1) Always use HTML, never plain markdown. (2) To embed a diagram, encode the Mermaid definition in base64 and use: <img src=\"https://mermaid.ink/img/<base64>\" alt=\"diagram description\" />",
            schema.object()
                .prop("name", schema.string().required())
                .prop("projectId", schema.integer().required())
                .prop("description", schema.string())
                .build(),
            args -> createTestPlan(
                args.path("name").asText(),
                args.path("projectId").asInt(),
                args.path("description").asText(null)
            )
        );

        server.registerTool("test_plan_update",
            "Update an existing test plan in Targetprocess by its numeric ID. All fields except id are optional — only provided (non-blank) fields are updated. stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'.",
            schema.object()
                .prop("id", schema.integer().required())
                .prop("name", schema.string())
                .prop("description", schema.string())
                .prop("stateName", schema.string())
                .build(),
            args -> updateTestPlan(
                args.path("id").asInt(),
                args.path("name").asText(null),
                args.path("description").asText(null),
                args.path("stateName").asText(null)
            )
        );

        server.registerTool("test_plan_get",
            "Get a test plan by its numeric ID. Returns full details including description.",
            schema.object()
                .prop("id", schema.integer().required())
                .build(),
            args -> getTestPlanById(args.path("id").asInt())
        );
    }

    public String searchTestPlans(String nameQuery, String projectName,
                                  String ownerLogin, String startDate,
                                  String endDate, int take) {
        List<TestPlanDto> testPlans = testPlanSearchService.searchTestPlans(
                nameQuery, projectName, ownerLogin, startDate, endDate, take);

        if (testPlans.isEmpty()) {
            return "No test plans found.";
        }

        return String.join("\n", testPlans.stream().map(this::format).toList());
    }

    public String createTestPlan(String name, int projectId, String description) {
        TestPlanDto testPlan = testPlanCreateService.createTestPlan(name, projectId, description);
        return "Created: " + format(testPlan);
    }

    public String updateTestPlan(int id, String name, String description, String stateName) {
        TestPlanDto testPlan = testPlanUpdateService.updateTestPlan(id, name, description, stateName);
        return "Updated: " + format(testPlan);
    }

    public String getTestPlanById(int id) {
        TestPlanDto testPlan = testPlanGetByIdService.getById(id);
        return format(testPlan) + "\nDescription:\n" + nullSafe(testPlan.description());
    }

    private String format(TestPlanDto t) {
        return "[%d] %s (Project: %s, State: %s, Owner: %s, Created: %s)"
            .formatted(
                t.id(), t.name(),
                nullSafe(t.projectName()), nullSafe(t.state()),
                nullSafe(t.ownerLogin()), nullSafe(t.createdAt())
            );
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}