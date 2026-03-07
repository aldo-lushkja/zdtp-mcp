package com.ibm.mcp.zdtp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
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

public class ZdtpMcpApplication {

    public static void main(String[] args) {
        TargetProcessProperties properties = TargetProcessProperties.fromEnv();
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        HttpClient javaHttpClient = HttpClient.newBuilder().build();
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(javaHttpClient, mapper);
        QueryEngine engine = new QueryEngine(properties, tpHttpClient, mapper);

        McpServer server = new McpServer();
        SchemaBuilder schema = new SchemaBuilder(mapper);

        // Domain: Epic
        EpicConverter epicConverter = new EpicConverter();
        EpicMcpTools epicMcpTools = new EpicMcpTools(
                new EpicSearchService(engine, epicConverter),
                new EpicCreateService(engine, epicConverter),
                new EpicUpdateService(engine, epicConverter),
                new EpicGetByIdService(engine, epicConverter)
        );

        // Domain: Feature
        FeatureConverter featureConverter = new FeatureConverter();
        FeatureMcpTools featureMcpTools = new FeatureMcpTools(
                new FeatureSearchService(engine, featureConverter),
                new FeatureCreateService(engine, featureConverter),
                new FeatureUpdateService(engine, featureConverter),
                new FeatureGetByIdService(engine, featureConverter)
        );

        // Domain: Project
        ProjectConverter projectConverter = new ProjectConverter();
        ProjectMcpTools projectMcpTools = new ProjectMcpTools(
                new ProjectSearchService(engine, projectConverter)
        );

        // Domain: Release
        ReleaseConverter releaseConverter = new ReleaseConverter();
        ReleaseMcpTools releaseMcpTools = new ReleaseMcpTools(
                new ReleaseSearchService(engine, releaseConverter),
                new ReleaseCreateService(engine, releaseConverter),
                new ReleaseUpdateService(engine, releaseConverter),
                new ReleaseGetByIdService(engine, releaseConverter)
        );

        // Domain: Request
        RequestConverter requestConverter = new RequestConverter();
        RequestMcpTools requestMcpTools = new RequestMcpTools(
                new RequestSearchService(engine, requestConverter),
                new RequestCreateService(engine, requestConverter),
                new RequestUpdateService(engine, requestConverter),
                new RequestGetByIdService(engine, requestConverter)
        );

        // Domain: Team
        TeamConverter teamConverter = new TeamConverter();
        TeamMcpTools teamMcpTools = new TeamMcpTools(
                new TeamSearchService(engine, teamConverter),
                new TeamGetByIdService(engine, teamConverter)
        );

        // Domain: TeamIteration
        TeamIterationConverter teamIterationConverter = new TeamIterationConverter();
        TeamIterationMcpTools teamIterationMcpTools = new TeamIterationMcpTools(
                new TeamIterationSearchService(engine, teamIterationConverter),
                new TeamIterationGetByIdService(engine, teamIterationConverter)
        );

        // Domain: TestCase
        TestCaseConverter testCaseConverter = new TestCaseConverter();
        TestStepConverter testStepConverter = new TestStepConverter();
        TestCaseMcpTools testCaseMcpTools = new TestCaseMcpTools(
                new TestCaseSearchService(engine, testCaseConverter),
                new TestCaseCreateService(engine, testCaseConverter),
                new TestCaseUpdateService(engine, testCaseConverter),
                new TestCaseGetByIdService(engine, testCaseConverter),
                new TestStepCreateService(engine, testStepConverter),
                new TestCaseDeleteService(engine),
                new TestStepDeleteService(engine)
        );

        // Domain: TestPlan
        TestPlanConverter testPlanConverter = new TestPlanConverter();
        TestPlanMcpTools testPlanMcpTools = new TestPlanMcpTools(
                new TestPlanSearchService(engine, testPlanConverter),
                new TestPlanCreateService(engine, testPlanConverter),
                new TestPlanUpdateService(engine, testPlanConverter),
                new TestPlanGetByIdService(engine, testPlanConverter),
                new TestPlanDeleteService(engine)
        );

        // Domain: UserStory
        UserStoryConverter userStoryConverter = new UserStoryConverter();
        UserStoryMcpTools userStoryMcpTools = new UserStoryMcpTools(
                new UserStorySearchService(engine, userStoryConverter),
                new UserStoryCreateService(engine, userStoryConverter),
                new UserStoryUpdateService(engine, userStoryConverter),
                new UserStoryGetByIdService(engine, userStoryConverter),
                new UserStoryDeleteService(engine)
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

        server.start();
    }
}
