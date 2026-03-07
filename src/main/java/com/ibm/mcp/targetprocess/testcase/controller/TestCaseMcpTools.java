package com.ibm.mcp.targetprocess.testcase.controller;

import com.ibm.mcp.targetprocess.testcase.dto.TestCaseDto;
import com.ibm.mcp.targetprocess.testcase.service.TestCaseCreateService;
import com.ibm.mcp.targetprocess.testcase.service.TestCaseGetByIdService;
import com.ibm.mcp.targetprocess.testcase.service.TestCaseSearchService;
import com.ibm.mcp.targetprocess.testcase.service.TestCaseUpdateService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestCaseMcpTools {

    private final TestCaseSearchService testCaseSearchService;
    private final TestCaseCreateService testCaseCreateService;
    private final TestCaseUpdateService testCaseUpdateService;
    private final TestCaseGetByIdService testCaseGetByIdService;

    public TestCaseMcpTools(TestCaseSearchService testCaseSearchService,
                            TestCaseCreateService testCaseCreateService,
                            TestCaseUpdateService testCaseUpdateService,
                            TestCaseGetByIdService testCaseGetByIdService) {
        this.testCaseSearchService = testCaseSearchService;
        this.testCaseCreateService = testCaseCreateService;
        this.testCaseUpdateService = testCaseUpdateService;
        this.testCaseGetByIdService = testCaseGetByIdService;
    }

    @Tool(description = """
            Search for test cases in Targetprocess. \
            Supports filtering by test case name, project name, owner login, \
            and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.""")
    public String searchTestCases(String nameQuery, String projectName,
                                  String ownerLogin, String startDate,
                                  String endDate, int take) {
        List<TestCaseDto> testCases = testCaseSearchService.searchTestCases(
                nameQuery, projectName, ownerLogin, startDate, endDate, take);

        if (testCases.isEmpty()) {
            return "No test cases found.";
        }

        return String.join("\n", testCases.stream().map(this::format).toList());
    }

    @Tool(description = """
            Create a new test case in Targetprocess. \
            Requires name and projectId (numeric ID of the project). \
            Description and testPlanId (numeric ID of the test plan) are optional. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String createTestCase(String name, int projectId, String description, Integer testPlanId) {
        TestCaseDto testCase = testCaseCreateService.createTestCase(name, projectId, description, testPlanId);
        return "Created: " + format(testCase);
    }

    @Tool(description = """
            Update an existing test case in Targetprocess by its numeric ID. \
            All fields except id are optional — only provided (non-blank) fields are updated. \
            stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String updateTestCase(int id, String name, String description, String stateName) {
        TestCaseDto testCase = testCaseUpdateService.updateTestCase(id, name, description, stateName);
        return "Updated: " + format(testCase);
    }

    @Tool(description = "Get a test case by its numeric ID. Returns full details including description.")
    public String getTestCaseById(int id) {
        TestCaseDto testCase = testCaseGetByIdService.getById(id);
        return format(testCase) + "\nDescription:\n" + nullSafe(testCase.description());
    }

    private String format(TestCaseDto t) {
        return "[%d] %s (Project: %s, State: %s, Owner: %s, TestPlan: %s, Created: %s)"
            .formatted(
                t.id(), t.name(),
                nullSafe(t.projectName()), nullSafe(t.state()),
                nullSafe(t.ownerLogin()), nullSafe(t.testPlanName()),
                nullSafe(t.createdAt())
            );
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}