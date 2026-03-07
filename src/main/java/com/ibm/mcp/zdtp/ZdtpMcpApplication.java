package com.ibm.mcp.zdtp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.epic.boundary.EpicMcpTools;
import com.ibm.mcp.zdtp.epic.control.*;
import com.ibm.mcp.zdtp.feature.boundary.FeatureMcpTools;
import com.ibm.mcp.zdtp.feature.control.*;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;
import com.ibm.mcp.zdtp.project.boundary.ProjectMcpTools;
import com.ibm.mcp.zdtp.project.control.ProjectConverter;
import com.ibm.mcp.zdtp.project.control.ProjectSearchService;
import com.ibm.mcp.zdtp.release.boundary.ReleaseMcpTools;
import com.ibm.mcp.zdtp.release.control.*;
import com.ibm.mcp.zdtp.request.boundary.RequestMcpTools;
import com.ibm.mcp.zdtp.request.control.*;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.team.boundary.TeamMcpTools;
import com.ibm.mcp.zdtp.team.control.TeamConverter;
import com.ibm.mcp.zdtp.team.control.TeamGetByIdService;
import com.ibm.mcp.zdtp.team.control.TeamSearchService;
import com.ibm.mcp.zdtp.teamiteration.boundary.TeamIterationMcpTools;
import com.ibm.mcp.zdtp.teamiteration.control.TeamIterationConverter;
import com.ibm.mcp.zdtp.teamiteration.control.TeamIterationGetByIdService;
import com.ibm.mcp.zdtp.teamiteration.control.TeamIterationSearchService;
import com.ibm.mcp.zdtp.testcase.boundary.TestCaseMcpTools;
import com.ibm.mcp.zdtp.testcase.control.*;
import com.ibm.mcp.zdtp.testplan.boundary.TestPlanMcpTools;
import com.ibm.mcp.zdtp.testplan.control.*;
import com.ibm.mcp.zdtp.userstory.boundary.UserStoryMcpTools;
import com.ibm.mcp.zdtp.userstory.control.*;

import java.net.http.HttpClient;
import java.time.Duration;

public class ZdtpMcpApplication {

    public static void main(String[] args) {
        TargetProcessProperties properties = TargetProcessProperties.fromEnv();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(client, mapper);

        // Domain: Epic
        EpicConverter epicConverter = new EpicConverter();
        EpicSearchService epicSearchService = new EpicSearchService(properties, tpHttpClient, epicConverter, mapper);
        EpicCreateService epicCreateService = new EpicCreateService(properties, tpHttpClient, epicConverter, mapper);
        EpicUpdateService epicUpdateService = new EpicUpdateService(properties, tpHttpClient, epicConverter, mapper);
        EpicGetByIdService epicGetByIdService = new EpicGetByIdService(properties, tpHttpClient, epicConverter, mapper);
        EpicMcpTools epicMcpTools = new EpicMcpTools(epicSearchService, epicCreateService, epicUpdateService, epicGetByIdService);

        // Domain: Feature
        FeatureConverter featureConverter = new FeatureConverter();
        FeatureSearchService featureSearchService = new FeatureSearchService(properties, tpHttpClient, featureConverter, mapper);
        FeatureCreateService featureCreateService = new FeatureCreateService(properties, tpHttpClient, featureConverter, mapper);
        FeatureUpdateService featureUpdateService = new FeatureUpdateService(properties, tpHttpClient, featureConverter, mapper);
        FeatureGetByIdService featureGetByIdService = new FeatureGetByIdService(properties, tpHttpClient, featureConverter, mapper);
        FeatureMcpTools featureMcpTools = new FeatureMcpTools(featureSearchService, featureCreateService, featureUpdateService, featureGetByIdService);

        // Domain: Project
        ProjectConverter projectConverter = new ProjectConverter();
        ProjectSearchService projectSearchService = new ProjectSearchService(properties, tpHttpClient, projectConverter, mapper);
        ProjectMcpTools projectMcpTools = new ProjectMcpTools(projectSearchService);

        // Domain: Release
        ReleaseConverter releaseConverter = new ReleaseConverter();
        ReleaseSearchService releaseSearchService = new ReleaseSearchService(properties, tpHttpClient, releaseConverter, mapper);
        ReleaseCreateService releaseCreateService = new ReleaseCreateService(properties, tpHttpClient, releaseConverter, mapper);
        ReleaseUpdateService releaseUpdateService = new ReleaseUpdateService(properties, tpHttpClient, releaseConverter, mapper);
        ReleaseGetByIdService releaseGetByIdService = new ReleaseGetByIdService(properties, tpHttpClient, releaseConverter, mapper);
        ReleaseMcpTools releaseMcpTools = new ReleaseMcpTools(releaseSearchService, releaseCreateService, releaseUpdateService, releaseGetByIdService);

        // Domain: Request
        RequestConverter requestConverter = new RequestConverter();
        RequestSearchService requestSearchService = new RequestSearchService(properties, tpHttpClient, requestConverter, mapper);
        RequestCreateService requestCreateService = new RequestCreateService(properties, tpHttpClient, requestConverter, mapper);
        RequestUpdateService requestUpdateService = new RequestUpdateService(properties, tpHttpClient, requestConverter, mapper);
        RequestGetByIdService requestGetByIdService = new RequestGetByIdService(properties, tpHttpClient, requestConverter, mapper);
        RequestMcpTools requestMcpTools = new RequestMcpTools(requestSearchService, requestCreateService, requestUpdateService, requestGetByIdService);

        // Domain: Team
        TeamConverter teamConverter = new TeamConverter();
        TeamSearchService teamSearchService = new TeamSearchService(properties, tpHttpClient, teamConverter, mapper);
        TeamGetByIdService teamGetByIdService = new TeamGetByIdService(properties, tpHttpClient, teamConverter, mapper);
        TeamMcpTools teamMcpTools = new TeamMcpTools(teamSearchService, teamGetByIdService);

        // Domain: TeamIteration
        TeamIterationConverter teamIterationConverter = new TeamIterationConverter();
        TeamIterationSearchService teamIterationSearchService = new TeamIterationSearchService(properties, tpHttpClient, teamIterationConverter, mapper);
        TeamIterationGetByIdService teamIterationGetByIdService = new TeamIterationGetByIdService(properties, tpHttpClient, teamIterationConverter, mapper);
        TeamIterationMcpTools teamIterationMcpTools = new TeamIterationMcpTools(teamIterationSearchService, teamIterationGetByIdService);

        // Domain: TestCase
        TestCaseConverter testCaseConverter = new TestCaseConverter();
        TestCaseSearchService testCaseSearchService = new TestCaseSearchService(properties, tpHttpClient, testCaseConverter, mapper);
        TestCaseCreateService testCaseCreateService = new TestCaseCreateService(properties, tpHttpClient, testCaseConverter, mapper);
        TestCaseUpdateService testCaseUpdateService = new TestCaseUpdateService(properties, tpHttpClient, testCaseConverter, mapper);
        TestCaseGetByIdService testCaseGetByIdService = new TestCaseGetByIdService(properties, tpHttpClient, testCaseConverter, mapper);
        
        TestStepConverter testStepConverter = new TestStepConverter();
        TestStepCreateService testStepCreateService = new TestStepCreateService(properties, tpHttpClient, testStepConverter, mapper);
        
        TestCaseDeleteService testCaseDeleteService = new TestCaseDeleteService(properties, tpHttpClient, mapper);
        TestStepDeleteService testStepDeleteService = new TestStepDeleteService(properties, tpHttpClient, mapper);
        
        TestCaseMcpTools testCaseMcpTools = new TestCaseMcpTools(testCaseSearchService, testCaseCreateService, testCaseUpdateService, testCaseGetByIdService, testStepCreateService, testCaseDeleteService, testStepDeleteService);

        // Domain: TestPlan
        TestPlanConverter testPlanConverter = new TestPlanConverter();
        TestPlanSearchService testPlanSearchService = new TestPlanSearchService(properties, tpHttpClient, testPlanConverter, mapper);
        TestPlanCreateService testPlanCreateService = new TestPlanCreateService(properties, tpHttpClient, testPlanConverter, mapper);
        TestPlanUpdateService testPlanUpdateService = new TestPlanUpdateService(properties, tpHttpClient, testPlanConverter, mapper);
        TestPlanGetByIdService testPlanGetByIdService = new TestPlanGetByIdService(properties, tpHttpClient, testPlanConverter, mapper);
        TestPlanDeleteService testPlanDeleteService = new TestPlanDeleteService(properties, tpHttpClient, mapper);
        TestPlanMcpTools testPlanMcpTools = new TestPlanMcpTools(testPlanSearchService, testPlanCreateService, testPlanUpdateService, testPlanGetByIdService, testPlanDeleteService);

        // Domain: UserStory
        UserStoryConverter userStoryConverter = new UserStoryConverter();
        UserStorySearchService userStorySearchService = new UserStorySearchService(properties, tpHttpClient, userStoryConverter, mapper);
        UserStoryCreateService userStoryCreateService = new UserStoryCreateService(properties, tpHttpClient, userStoryConverter, mapper);
        UserStoryUpdateService userStoryUpdateService = new UserStoryUpdateService(properties, tpHttpClient, userStoryConverter, mapper);
        UserStoryGetByIdService userStoryGetByIdService = new UserStoryGetByIdService(properties, tpHttpClient, userStoryConverter, mapper);
        UserStoryDeleteService userStoryDeleteService = new UserStoryDeleteService(properties, tpHttpClient, mapper);
        UserStoryMcpTools userStoryMcpTools = new UserStoryMcpTools(userStorySearchService, userStoryCreateService, userStoryUpdateService, userStoryGetByIdService, userStoryDeleteService);

        McpServer server = new McpServer();
        SchemaBuilder schema = new SchemaBuilder(mapper);

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
