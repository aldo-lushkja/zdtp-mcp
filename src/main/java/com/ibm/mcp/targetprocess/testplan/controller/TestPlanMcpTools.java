package com.ibm.mcp.targetprocess.testplan.controller;

import com.ibm.mcp.targetprocess.testplan.dto.TestPlanDto;
import com.ibm.mcp.targetprocess.testplan.service.TestPlanCreateService;
import com.ibm.mcp.targetprocess.testplan.service.TestPlanSearchService;
import com.ibm.mcp.targetprocess.testplan.service.TestPlanUpdateService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestPlanMcpTools {

    private final TestPlanSearchService testPlanSearchService;
    private final TestPlanCreateService testPlanCreateService;
    private final TestPlanUpdateService testPlanUpdateService;

    public TestPlanMcpTools(TestPlanSearchService testPlanSearchService,
                            TestPlanCreateService testPlanCreateService,
                            TestPlanUpdateService testPlanUpdateService) {
        this.testPlanSearchService = testPlanSearchService;
        this.testPlanCreateService = testPlanCreateService;
        this.testPlanUpdateService = testPlanUpdateService;
    }

    @Tool(description = """
            Search for test plans in Targetprocess. \
            Supports filtering by test plan name, project name, owner login, \
            and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.""")
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

    @Tool(description = """
            Create a new test plan in Targetprocess. \
            Requires name and projectId (numeric ID of the project). \
            Description is optional. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String createTestPlan(String name, int projectId, String description) {
        TestPlanDto testPlan = testPlanCreateService.createTestPlan(name, projectId, description);
        return "Created: " + format(testPlan);
    }

    @Tool(description = """
            Update an existing test plan in Targetprocess by its numeric ID. \
            All fields except id are optional — only provided (non-blank) fields are updated. \
            stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String updateTestPlan(int id, String name, String description, String stateName) {
        TestPlanDto testPlan = testPlanUpdateService.updateTestPlan(id, name, description, stateName);
        return "Updated: " + format(testPlan);
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