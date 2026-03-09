package com.ibm.mcp.zdtp.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;
import com.ibm.mcp.zdtp.bug.boundary.BugMcpTools;
import com.ibm.mcp.zdtp.bug.control.*;
import com.ibm.mcp.zdtp.comment.boundary.CommentMcpTools;
import com.ibm.mcp.zdtp.comment.control.*;
import com.ibm.mcp.zdtp.epic.boundary.EpicMcpTools;
import com.ibm.mcp.zdtp.epic.control.*;
import com.ibm.mcp.zdtp.feature.boundary.FeatureMcpTools;
import com.ibm.mcp.zdtp.feature.control.*;
import com.ibm.mcp.zdtp.project.boundary.ProjectMcpTools;
import com.ibm.mcp.zdtp.project.control.*;
import com.ibm.mcp.zdtp.relation.boundary.RelationMcpTools;
import com.ibm.mcp.zdtp.relation.control.*;
import com.ibm.mcp.zdtp.release.boundary.ReleaseMcpTools;
import com.ibm.mcp.zdtp.release.control.*;
import com.ibm.mcp.zdtp.request.boundary.RequestMcpTools;
import com.ibm.mcp.zdtp.request.control.*;
import com.ibm.mcp.zdtp.task.boundary.TaskMcpTools;
import com.ibm.mcp.zdtp.task.control.*;
import com.ibm.mcp.zdtp.team.boundary.TeamMcpTools;
import com.ibm.mcp.zdtp.team.control.*;
import com.ibm.mcp.zdtp.teamiteration.boundary.TeamIterationMcpTools;
import com.ibm.mcp.zdtp.teamiteration.control.*;
import com.ibm.mcp.zdtp.testcase.boundary.TestCaseMcpTools;
import com.ibm.mcp.zdtp.testcase.control.*;
import com.ibm.mcp.zdtp.testplan.boundary.TestPlanMcpTools;
import com.ibm.mcp.zdtp.testplan.control.*;
import com.ibm.mcp.zdtp.user.boundary.UserMcpTools;
import com.ibm.mcp.zdtp.user.control.*;
import com.ibm.mcp.zdtp.userstory.boundary.UserStoryMcpTools;
import com.ibm.mcp.zdtp.userstory.control.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Integration test that wires ALL boundary modules (exactly like ZdtpMcpApplication)
 * and verifies: (1) all expected tools are registered via tools/list,
 * (2) each search tool responds without error to a tools/call with empty args,
 * (3) unknown tools return an isError response.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
class McpServerIntegrationTest {

    private static final String EMPTY_ITEMS = "{\"Items\":[]}";

    /** All 51 tool names registered across the 15 boundary modules. */
    private static final List<String> ALL_TOOL_NAMES = List.of(
            // Epic (5)
            "epic_search", "epic_create", "epic_update", "epic_get", "epic_delete",
            // Feature (5)
            "feature_search", "feature_create", "feature_update", "feature_get", "feature_delete",
            // Project (1)
            "project_search",
            // Release (5)
            "release_search", "release_create", "release_update", "release_get", "release_delete",
            // Request (5)
            "request_search", "request_create", "request_update", "request_get", "request_delete",
            // Team (2)
            "team_search", "team_get",
            // TeamIteration (2)
            "team_iteration_search", "team_iteration_get",
            // TestCase (6)
            "test_case_search", "test_case_create", "test_case_update", "test_case_get", "test_case_delete", "test_step_create", "test_step_delete",
            // TestPlan (5)
            "test_plan_search", "test_plan_create", "test_plan_update", "test_plan_get", "test_plan_delete",
            // UserStory (5)
            "user_story_search", "user_story_create", "user_story_update", "user_story_get", "user_story_delete",
            // Comment (1)
            "comment_add",
            // Bug (5)
            "bug_search", "bug_create", "bug_update", "bug_get", "bug_delete",
            // Task (5)
            "task_search", "task_create", "task_update", "task_get", "task_delete",
            // User (1)
            "user_search",
            // Relation (2)
            "relation_search", "relation_link"
    );

    /** Search tools that accept empty/optional arguments — used for smoke-test calls. */
    private static final List<String> SEARCH_TOOLS = List.of(
            "epic_search", "feature_search", "project_search", "release_search",
            "request_search", "team_search", "team_iteration_search",
            "test_case_search", "test_plan_search", "user_story_search", "bug_search", "task_search"
    );

    @Mock TargetProcessHttpClient httpClient;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        // Return empty Items for any API call so search handlers don't throw
        when(httpClient.fetch(any())).thenReturn(EMPTY_ITEMS);
        when(httpClient.parse(any(), any())).thenCallRealMethod();
    }

    // ── tools/list ──────────────────────────────────────────────────────

    @Test
    void toolsList_returnsAll51Tools() throws Exception {
        String request = jsonRpc("tools/list", mapper.createObjectNode(), 1);
        JsonNode response = sendAndReceive(request);

        assertThat(response.has("error")).isFalse();

        JsonNode tools = response.path("result").path("tools");
        assertThat(tools.isArray()).isTrue();

        Set<String> registered = new HashSet<>();
        for (JsonNode tool : tools) {
            registered.add(tool.path("name").asText());
        }

        assertThat(registered).containsExactlyInAnyOrderElementsOf(ALL_TOOL_NAMES);
    }

    // ── tools/call — search tools (smoke) ───────────────────────────────

    @Test
    void toolsCall_searchToolsRespondWithoutError() throws Exception {
        StringBuilder input = new StringBuilder();
        int id = 10;
        for (String toolName : SEARCH_TOOLS) {
            input.append(toolCallJson(toolName, "{}", id++)).append("\n");
        }

        List<JsonNode> responses = sendAndReceiveAll(input.toString(), SEARCH_TOOLS.size());

        for (int i = 0; i < responses.size(); i++) {
            JsonNode resp = responses.get(i);
            assertThat(resp.has("error"))
                    .as("Tool '%s' returned a JSON-RPC error", SEARCH_TOOLS.get(i))
                    .isFalse();
            assertThat(resp.path("result").path("isError").asBoolean(false))
                    .as("Tool '%s' returned isError=true: %s", SEARCH_TOOLS.get(i),
                            resp.path("result").path("content").path(0).path("text").asText())
                    .isFalse();
        }
    }

    // ── tools/call — unknown tool ───────────────────────────────────────

    @Test
    void toolsCall_unknownToolReturnsIsError() throws Exception {
        String request = toolCallJson("does_not_exist", "{}", 99);
        JsonNode response = sendAndReceive(request);

        assertThat(response.path("result").path("isError").asBoolean(false)).isTrue();
        assertThat(response.path("result").path("content").path(0).path("text").asText())
                .contains("Unknown tool");
    }

    // ── initialize ──────────────────────────────────────────────────────

    @Test
    void initialize_returnsServerInfo() throws Exception {
        String request = jsonRpc("initialize", mapper.createObjectNode(), 0);
        JsonNode response = sendAndReceive(request);

        assertThat(response.path("result").path("serverInfo").path("name").asText()).isEqualTo("zdtp-mcp");
        assertThat(response.path("result").path("protocolVersion").asText()).isEqualTo("2024-11-05");
    }

    // ── Plumbing ────────────────────────────────────────────────────────

    /**
     * Pipes one JSON-RPC line through a fresh McpServer instance and returns the parsed response.
     */
    private JsonNode sendAndReceive(String jsonRpcLine) throws Exception {
        List<JsonNode> results = sendAndReceiveAll(jsonRpcLine, 1);
        return results.get(0);
    }

    private List<JsonNode> sendAndReceiveAll(String input, int expectedResponses) throws Exception {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            ByteArrayOutputStream capture = new ByteArrayOutputStream();
            System.setOut(new PrintStream(capture));

            McpServer server = buildFullServer();
            server.start();

            System.setIn(originalIn);
            System.setOut(originalOut);

            String[] lines = capture.toString().split("\\R");
            List<JsonNode> responses = new ArrayList<>();
            for (String line : lines) {
                if (!line.isBlank()) responses.add(mapper.readTree(line));
            }
            assertThat(responses).hasSize(expectedResponses);
            return responses;
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    /**
     * Builds and wires the full MCP server exactly as ZdtpMcpApplication does.
     */
    private McpServer buildFullServer() {
        TargetProcessProperties props = new TargetProcessProperties("https://test.tpondemand.com", "test-token");
        QueryEngine engine = new QueryEngine(props, httpClient, mapper);
        McpServer server = new McpServer();
        SchemaBuilder schema = new SchemaBuilder(mapper);

        // Epic
        EpicConverter epicC = new EpicConverter();
        new EpicMcpTools(new EpicSearchService(engine, epicC), new EpicCreateService(engine, epicC),
                new EpicUpdateService(engine, epicC), new EpicGetByIdService(engine, epicC),
                new EpicDeleteService(engine)).register(server, schema);

        // Feature
        FeatureConverter featureC = new FeatureConverter();
        new FeatureMcpTools(new FeatureSearchService(engine, featureC), new FeatureCreateService(engine, featureC),
                new FeatureUpdateService(engine, featureC), new FeatureGetByIdService(engine, featureC),
                new FeatureDeleteService(engine)).register(server, schema);

        // Project
        ProjectConverter projectC = new ProjectConverter();
        new ProjectMcpTools(new ProjectSearchService(engine, projectC)).register(server, schema);

        // Release
        ReleaseConverter releaseC = new ReleaseConverter();
        new ReleaseMcpTools(new ReleaseSearchService(engine, releaseC), new ReleaseCreateService(engine, releaseC),
                new ReleaseUpdateService(engine, releaseC), new ReleaseGetByIdService(engine, releaseC),
                new ReleaseDeleteService(engine)).register(server, schema);

        // Request
        RequestConverter requestC = new RequestConverter();
        new RequestMcpTools(new RequestSearchService(engine, requestC), new RequestCreateService(engine, requestC),
                new RequestUpdateService(engine, requestC), new RequestGetByIdService(engine, requestC),
                new RequestDeleteService(engine)).register(server, schema);

        // Team
        TeamConverter teamC = new TeamConverter();
        new TeamMcpTools(new TeamSearchService(engine, teamC), new TeamGetByIdService(engine, teamC)).register(server, schema);

        // TeamIteration
        TeamIterationConverter tiC = new TeamIterationConverter();
        new TeamIterationMcpTools(new TeamIterationSearchService(engine, tiC),
                new TeamIterationGetByIdService(engine, tiC)).register(server, schema);

        // TestCase
        TestCaseConverter tcC = new TestCaseConverter();
        TestStepConverter tsC = new TestStepConverter();
        new TestCaseMcpTools(new TestCaseSearchService(engine, tcC), new TestCaseCreateService(engine, tcC),
                new TestCaseUpdateService(engine, tcC), new TestCaseGetByIdService(engine, tcC),
                new TestStepCreateService(engine, tsC), new TestCaseDeleteService(engine),
                new TestStepDeleteService(engine)).register(server, schema);

        // TestPlan
        TestPlanConverter tpC = new TestPlanConverter();
        new TestPlanMcpTools(new TestPlanSearchService(engine, tpC), new TestPlanCreateService(engine, tpC),
                new TestPlanUpdateService(engine, tpC), new TestPlanGetByIdService(engine, tpC),
                new TestPlanDeleteService(engine)).register(server, schema);

        // UserStory
        UserStoryConverter usC = new UserStoryConverter();
        new UserStoryMcpTools(new UserStorySearchService(engine, usC), new UserStoryCreateService(engine, usC),
                new UserStoryUpdateService(engine, usC), new UserStoryGetByIdService(engine, usC),
                new UserStoryDeleteService(engine)).register(server, schema);

        // Comment
        CommentConverter cmC = new CommentConverter();
        new CommentMcpTools(new CommentCreateService(engine, cmC)).register(server, schema);

        // Bug
        BugConverter bugC = new BugConverter();
        new BugMcpTools(new BugSearchService(engine, bugC), new BugCreateService(engine, bugC),
                new BugUpdateService(engine, bugC), new BugGetByIdService(engine, bugC),
                new BugDeleteService(engine)).register(server, schema);

        // Task
        TaskConverter taskC = new TaskConverter();
        new TaskMcpTools(new TaskSearchService(engine, taskC), new TaskCreateService(engine, taskC),
                new TaskUpdateService(engine, taskC), new TaskGetByIdService(engine, taskC),
                new TaskDeleteService(engine)).register(server, schema);

        // User
        UserConverter userC = new UserConverter();
        new UserMcpTools(new UserSearchService(engine, userC)).register(server, schema);

        // Relation
        RelationConverter relC = new RelationConverter();
        new RelationMcpTools(new RelationSearchService(engine, relC),
                new RelationCreateService(engine, relC)).register(server, schema);

        return server;
    }

    private String jsonRpc(String method, JsonNode params, int id) throws Exception {
        return mapper.writeValueAsString(mapper.createObjectNode()
                .put("jsonrpc", "2.0")
                .put("id", id)
                .put("method", method)
                .set("params", params));
    }

    private String toolCallJson(String toolName, String argsJson, int id) throws Exception {
        var params = mapper.createObjectNode();
        params.put("name", toolName);
        params.set("arguments", mapper.readTree(argsJson));
        return jsonRpc("tools/call", params, id);
    }
}
