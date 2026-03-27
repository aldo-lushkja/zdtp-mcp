package com.ibm.mcp.zdtp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.comment.boundary.CommentMcpTools;
import com.ibm.mcp.zdtp.comment.control.*;
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
        var properties = TargetProcessProperties.fromEnv();
        var mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var javaHttpClient = HttpClient.newBuilder()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();
        var tpHttpClient = new TargetProcessHttpClient(javaHttpClient, mapper);
        var engine = new QueryEngine(properties, tpHttpClient, mapper);

        var server = new McpServer();
        var schema = new SchemaBuilder(mapper);

        // Domain: Epic
        var epicConverter = new EpicConverter();
        var epicMcpTools = new EpicMcpTools(
                new EpicSearchService(engine, epicConverter),
                new EpicCreateService(engine, epicConverter),
                new EpicUpdateService(engine, epicConverter),
                new EpicGetByIdService(engine, epicConverter),
                new EpicDeleteService(engine)
        );

        // Domain: Feature
        var featureConverter = new FeatureConverter();
        var featureMcpTools = new FeatureMcpTools(
                new FeatureSearchService(engine, featureConverter),
                new FeatureCreateService(engine, featureConverter),
                new FeatureUpdateService(engine, featureConverter),
                new FeatureGetByIdService(engine, featureConverter),
                new FeatureDeleteService(engine)
        );

        // Domain: Project
        var projectConverter = new ProjectConverter();
        var projectMcpTools = new ProjectMcpTools(
                new ProjectSearchService(engine, projectConverter)
        );

        // Domain: Release
        var releaseConverter = new ReleaseConverter();
        var releaseMcpTools = new ReleaseMcpTools(
                new ReleaseSearchService(engine, releaseConverter),
                new ReleaseCreateService(engine, releaseConverter),
                new ReleaseUpdateService(engine, releaseConverter),
                new ReleaseGetByIdService(engine, releaseConverter),
                new ReleaseDeleteService(engine)
        );

        // Domain: Request
        var requestConverter = new RequestConverter();
        var requestMcpTools = new RequestMcpTools(
                new RequestSearchService(engine, requestConverter),
                new RequestCreateService(engine, requestConverter),
                new RequestUpdateService(engine, requestConverter),
                new RequestGetByIdService(engine, requestConverter),
                new RequestDeleteService(engine)
        );

        // Domain: Team
        var teamConverter = new TeamConverter();
        var teamMcpTools = new TeamMcpTools(
                new TeamSearchService(engine, teamConverter),
                new TeamGetByIdService(engine, teamConverter)
        );

        // Domain: TeamIteration
        var teamIterationConverter = new TeamIterationConverter();
        var teamIterationMcpTools = new TeamIterationMcpTools(
                new TeamIterationSearchService(engine, teamIterationConverter),
                new TeamIterationGetByIdService(engine, teamIterationConverter)
        );

        // Domain: TestCase
        var testCaseConverter = new TestCaseConverter();
        var testStepConverter = new TestStepConverter();
        var testCaseMcpTools = new TestCaseMcpTools(
                new TestCaseSearchService(engine, testCaseConverter),
                new TestCaseCreateService(engine, testCaseConverter),
                new TestCaseUpdateService(engine, testCaseConverter),
                new TestCaseGetByIdService(engine, testCaseConverter),
                new TestStepCreateService(engine, testStepConverter),
                new TestCaseDeleteService(engine),
                new TestStepDeleteService(engine)
        );

        // Domain: TestPlan
        var testPlanConverter = new TestPlanConverter();
        var testPlanMcpTools = new TestPlanMcpTools(
                new TestPlanSearchService(engine, testPlanConverter),
                new TestPlanCreateService(engine, testPlanConverter),
                new TestPlanUpdateService(engine, testPlanConverter),
                new TestPlanGetByIdService(engine, testPlanConverter),
                new TestPlanDeleteService(engine)
        );

        // Domain: UserStory
        var userStoryConverter = new UserStoryConverter();
        var userStoryMcpTools = new UserStoryMcpTools(
                new UserStorySearchService(engine, userStoryConverter),
                new UserStoryCreateService(engine, userStoryConverter),
                new UserStoryUpdateService(engine, userStoryConverter),
                new UserStoryGetByIdService(engine, userStoryConverter),
                new UserStoryDeleteService(engine)
        );

        // Domain: Comment
        var commentConverter = new CommentConverter();
        var commentMcpTools = new CommentMcpTools(
                new CommentCreateService(engine, commentConverter)
        );

        // Domain: Bug
        var bugConverter = new BugConverter();
        var bugMcpTools = new BugMcpTools(
                new BugSearchService(engine, bugConverter),
                new BugCreateService(engine, bugConverter),
                new BugUpdateService(engine, bugConverter),
                new BugGetByIdService(engine, bugConverter),
                new BugDeleteService(engine)
        );

        // Domain: Task
        var taskConverter = new TaskConverter();
        var taskMcpTools = new TaskMcpTools(
                new TaskSearchService(engine, taskConverter),
                new TaskCreateService(engine, taskConverter),
                new TaskUpdateService(engine, taskConverter),
                new TaskGetByIdService(engine, taskConverter),
                new TaskDeleteService(engine)
        );

        // Domain: User
        var userConverter = new UserConverter();
        var userMcpTools = new UserMcpTools(
                new UserSearchService(engine, userConverter)
        );

        // Domain: Relation
        var relationConverter = new RelationConverter();
        var relationMcpTools = new RelationMcpTools(
                new RelationSearchService(engine, relationConverter),
                new RelationCreateService(engine, relationConverter)
        );

        // Register server meta tools
        server.registerTool(
            "server_changelog",
            "Returns the full zdtp-mcp changelog listing all versions and their changes.",
            schema.object().build(),
            ignored -> {
                try (var is = ZdtpMcpApplication.class.getResourceAsStream("/CHANGELOG.md")) {
                    if (is == null) return "Changelog not available.";
                    return new String(is.readAllBytes(), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    return "Failed to read changelog: " + e.getMessage();
                }
            }
        );

        // Register tools
        epicMcpTools.register(server, schema);
        featureMcpTools.register(server, schema);
        projectMcpTools.register(server, schema);
        releaseMcpTools.register(server, schema);
        requestMcpTools.register(server, schema);
        teamMcpTools.register(server, schema);
        teamIterationMcpTools.register(server, schema);
        testCaseMcpTools.register(server, schema);
        testPlanMcpTools.register(server, schema);
        userStoryMcpTools.register(server, schema);
        commentMcpTools.register(server, schema);
        bugMcpTools.register(server, schema);
        taskMcpTools.register(server, schema);
        userMcpTools.register(server, schema);
        relationMcpTools.register(server, schema);

        server.start();
    }
}
