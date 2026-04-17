package com.ibm.mcp.zdtp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.comment.boundary.CommentMcpTools;
import com.ibm.mcp.zdtp.comment.control.CommentConverter;
import com.ibm.mcp.zdtp.comment.control.CommentCreateService;
import com.ibm.mcp.zdtp.bug.boundary.BugMcpTools;
import com.ibm.mcp.zdtp.bug.control.*;
import com.ibm.mcp.zdtp.task.boundary.TaskMcpTools;
import com.ibm.mcp.zdtp.task.control.*;
import com.ibm.mcp.zdtp.user.boundary.UserMcpTools;
import com.ibm.mcp.zdtp.user.control.*;
import com.ibm.mcp.zdtp.relation.boundary.RelationMcpTools;
import com.ibm.mcp.zdtp.relation.control.*;
import com.ibm.mcp.zdtp.epic.boundary.EpicMcpTools;
import com.ibm.mcp.zdtp.epic.control.*;
import com.ibm.mcp.zdtp.feature.boundary.FeatureMcpTools;
import com.ibm.mcp.zdtp.feature.control.*;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;
import com.ibm.mcp.zdtp.project.boundary.ProjectMcpTools;
import com.ibm.mcp.zdtp.project.control.*;
import com.ibm.mcp.zdtp.release.boundary.ReleaseMcpTools;
import com.ibm.mcp.zdtp.release.control.*;
import com.ibm.mcp.zdtp.request.boundary.RequestMcpTools;
import com.ibm.mcp.zdtp.request.control.*;
import com.ibm.mcp.zdtp.team.boundary.TeamMcpTools;
import com.ibm.mcp.zdtp.team.control.*;
import com.ibm.mcp.zdtp.teamiteration.boundary.TeamIterationMcpTools;
import com.ibm.mcp.zdtp.teamiteration.control.*;
import com.ibm.mcp.zdtp.testcase.boundary.TestCaseMcpTools;
import com.ibm.mcp.zdtp.testcase.control.*;
import com.ibm.mcp.zdtp.testplan.boundary.TestPlanMcpTools;
import com.ibm.mcp.zdtp.testplan.control.*;
import com.ibm.mcp.zdtp.userstory.boundary.UserStoryMcpTools;
import com.ibm.mcp.zdtp.userstory.control.*;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class ZdtpMcpApplication {

    public static void main(String[] args) {
        var mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        var httpClient = HttpClient.newBuilder()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();

        var tpClient = new TargetProcessHttpClient(httpClient, mapper);
        var engine = new QueryEngine(TargetProcessProperties.fromEnv(), tpClient, mapper);

        var server = new McpServer();
        var schema = new SchemaBuilder(mapper);

        registerTools(engine, server, schema);
        server.start();
    }

    private static void registerTools(QueryEngine engine, McpServer server, SchemaBuilder schema) {
        // Server meta tool
        server.registerTool("server_changelog",
            "Returns the full zdtp-mcp changelog.",
            schema.object().build(), ignored -> {
                try (var is = ZdtpMcpApplication.class.getResourceAsStream("/CHANGELOG.md")) {
                    return is == null ? "Changelog not available." 
                        : new String(is.readAllBytes(), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    return "Failed to read changelog: " + e.getMessage();
                }
            });

        // Domain tools
        var ec = new EpicConverter();
        epics(engine, ec).register(server, schema);

        var fc = new FeatureConverter();
        features(engine, fc).register(server, schema);

        var pc = new ProjectConverter();
        projects(engine, pc).register(server, schema);

        var rc = new ReleaseConverter();
        releases(engine, rc).register(server, schema);

        var reqc = new RequestConverter();
        requests(engine, reqc).register(server, schema);

        var tc = new TeamConverter();
        teams(engine, tc).register(server, schema);

        var tic = new TeamIterationConverter();
        teamIterations(engine, tic).register(server, schema);

        var tcc = new TestCaseConverter();
        var tsc = new TestStepConverter();
        testCases(engine, tcc, tsc).register(server, schema);

        var tplc = new TestPlanConverter();
        testPlans(engine, tplc).register(server, schema);

        var usc = new UserStoryConverter();
        userStories(engine, usc).register(server, schema);

        var cc = new CommentConverter();
        comments(engine, cc).register(server, schema);

        var bc = new BugConverter();
        bugs(engine, bc).register(server, schema);

        var taskc = new TaskConverter();
        tasks(engine, taskc).register(server, schema);

        var uc = new UserConverter();
        users(engine, uc).register(server, schema);

        var relc = new RelationConverter();
        relations(engine, relc).register(server, schema);
    }

    // Factory methods for each domain
    private static EpicMcpTools epics(QueryEngine e, EpicConverter c) {
        return new EpicMcpTools(new EpicSearchService(e, c), new EpicCreateService(e, c),
            new EpicUpdateService(e, c), new EpicGetByIdService(e, c), new EpicDeleteService(e));
    }

    private static FeatureMcpTools features(QueryEngine e, FeatureConverter c) {
        return new FeatureMcpTools(new FeatureSearchService(e, c), new FeatureCreateService(e, c),
            new FeatureUpdateService(e, c), new FeatureGetByIdService(e, c), new FeatureDeleteService(e));
    }

    private static ProjectMcpTools projects(QueryEngine e, ProjectConverter c) {
        return new ProjectMcpTools(new ProjectSearchService(e, c));
    }

    private static ReleaseMcpTools releases(QueryEngine e, ReleaseConverter c) {
        return new ReleaseMcpTools(new ReleaseSearchService(e, c), new ReleaseCreateService(e, c),
            new ReleaseUpdateService(e, c), new ReleaseGetByIdService(e, c), new ReleaseDeleteService(e));
    }

    private static RequestMcpTools requests(QueryEngine e, RequestConverter c) {
        return new RequestMcpTools(new RequestSearchService(e, c), new RequestCreateService(e, c),
            new RequestUpdateService(e, c), new RequestGetByIdService(e, c), new RequestDeleteService(e));
    }

    private static TeamMcpTools teams(QueryEngine e, TeamConverter c) {
        return new TeamMcpTools(new TeamSearchService(e, c), new TeamGetByIdService(e, c));
    }

    private static TeamIterationMcpTools teamIterations(QueryEngine e, TeamIterationConverter c) {
        return new TeamIterationMcpTools(new TeamIterationSearchService(e, c), new TeamIterationGetByIdService(e, c));
    }

    private static TestCaseMcpTools testCases(QueryEngine e, TestCaseConverter c, TestStepConverter s) {
        return new TestCaseMcpTools(new TestCaseSearchService(e, c), new TestCaseCreateService(e, c),
            new TestCaseUpdateService(e, c), new TestCaseGetByIdService(e, c),
            new TestStepCreateService(e, s), new TestCaseDeleteService(e), new TestStepDeleteService(e));
    }

    private static TestPlanMcpTools testPlans(QueryEngine e, TestPlanConverter c) {
        return new TestPlanMcpTools(new TestPlanSearchService(e, c), new TestPlanCreateService(e, c),
            new TestPlanUpdateService(e, c), new TestPlanGetByIdService(e, c), new TestPlanDeleteService(e));
    }

    private static UserStoryMcpTools userStories(QueryEngine e, UserStoryConverter c) {
        return new UserStoryMcpTools(new UserStorySearchService(e, c), new UserStoryCreateService(e, c),
            new UserStoryUpdateService(e, c), new UserStoryGetByIdService(e, c), new UserStoryDeleteService(e));
    }

    private static CommentMcpTools comments(QueryEngine e, CommentConverter c) {
        return new CommentMcpTools(new CommentCreateService(e, c));
    }

    private static BugMcpTools bugs(QueryEngine e, BugConverter c) {
        return new BugMcpTools(new BugSearchService(e, c), new BugCreateService(e, c),
            new BugUpdateService(e, c), new BugGetByIdService(e, c), new BugDeleteService(e));
    }

    private static TaskMcpTools tasks(QueryEngine e, TaskConverter c) {
        return new TaskMcpTools(new TaskSearchService(e, c), new TaskCreateService(e, c),
            new TaskUpdateService(e, c), new TaskGetByIdService(e, c), new TaskDeleteService(e));
    }

    private static UserMcpTools users(QueryEngine e, UserConverter c) {
        return new UserMcpTools(new UserSearchService(e, c));
    }

    private static RelationMcpTools relations(QueryEngine e, RelationConverter c) {
        return new RelationMcpTools(new RelationSearchService(e, c), new RelationCreateService(e, c));
    }
}