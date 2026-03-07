package com.ibm.mcp.zdtp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

// Import all bounded contexts
import com.ibm.mcp.zdtp.epic.boundary.EpicMcpTools;
import com.ibm.mcp.zdtp.epic.control.*;
import com.ibm.mcp.zdtp.feature.boundary.FeatureMcpTools;
import com.ibm.mcp.zdtp.feature.control.*;
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
import java.time.Duration;

public class ZdtpMcpApplication {
    public static void main(String[] args) {
        String baseUrl = System.getenv("TARGETPROCESS_BASE_URL");
        String token = System.getenv("TARGETPROCESS_ACCESS_TOKEN");
        
        if (baseUrl == null || token == null) {
            System.err.println("Error: Required environment variables TARGETPROCESS_BASE_URL and TARGETPROCESS_ACCESS_TOKEN are missing!");
            System.exit(1);
        }

        TargetProcessProperties properties = new TargetProcessProperties(baseUrl, token);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
                
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(client, mapper);

        // Wiring Epic
        EpicConverter epicConverter = new EpicConverter();
        EpicSearchService epicSearchService = new EpicSearchService(properties, tpHttpClient, epicConverter);
        EpicCreateService epicCreateService = new EpicCreateService(properties, tpHttpClient, epicConverter, mapper);
        EpicUpdateService epicUpdateService = new EpicUpdateService(properties, tpHttpClient, epicConverter, mapper);
        EpicGetByIdService epicGetByIdService = new EpicGetByIdService(properties, tpHttpClient, epicConverter);
        EpicMcpTools epicMcpTools = new EpicMcpTools(epicSearchService, epicCreateService, epicUpdateService, epicGetByIdService);

        // Wiring Feature
        FeatureConverter featureConverter = new FeatureConverter();
        FeatureSearchService featureSearchService = new FeatureSearchService(properties, tpHttpClient, featureConverter);
        FeatureCreateService featureCreateService = new FeatureCreateService(properties, tpHttpClient, featureConverter, mapper);
        FeatureUpdateService featureUpdateService = new FeatureUpdateService(properties, tpHttpClient, featureConverter, mapper);
        FeatureGetByIdService featureGetByIdService = new FeatureGetByIdService(properties, tpHttpClient, featureConverter);
        FeatureMcpTools featureMcpTools = new FeatureMcpTools(featureSearchService, featureCreateService, featureUpdateService, featureGetByIdService);
        
        // Wiring Project
        ProjectConverter projectConverter = new ProjectConverter();
        ProjectSearchService projectSearchService = new ProjectSearchService(properties, tpHttpClient, projectConverter);
        ProjectMcpTools projectMcpTools = new ProjectMcpTools(projectSearchService);
        
        // Wiring Release
        ReleaseConverter releaseConverter = new ReleaseConverter();
        ReleaseSearchService releaseSearchService = new ReleaseSearchService(properties, tpHttpClient, releaseConverter);
        ReleaseCreateService releaseCreateService = new ReleaseCreateService(properties, tpHttpClient, releaseConverter, mapper);
        ReleaseUpdateService releaseUpdateService = new ReleaseUpdateService(properties, tpHttpClient, releaseConverter, mapper);
        ReleaseGetByIdService releaseGetByIdService = new ReleaseGetByIdService(properties, tpHttpClient, releaseConverter);
        ReleaseMcpTools releaseMcpTools = new ReleaseMcpTools(releaseSearchService, releaseCreateService, releaseUpdateService, releaseGetByIdService);
        
        // Wiring Request
        RequestConverter requestConverter = new RequestConverter();
        RequestSearchService requestSearchService = new RequestSearchService(properties, tpHttpClient, requestConverter);
        RequestCreateService requestCreateService = new RequestCreateService(properties, tpHttpClient, requestConverter, mapper);
        RequestUpdateService requestUpdateService = new RequestUpdateService(properties, tpHttpClient, requestConverter, mapper);
        RequestGetByIdService requestGetByIdService = new RequestGetByIdService(properties, tpHttpClient, requestConverter);
        RequestMcpTools requestMcpTools = new RequestMcpTools(requestSearchService, requestCreateService, requestUpdateService, requestGetByIdService);

        // Wiring Team
        TeamConverter teamConverter = new TeamConverter();
        TeamSearchService teamSearchService = new TeamSearchService(properties, tpHttpClient, teamConverter);
        TeamGetByIdService teamGetByIdService = new TeamGetByIdService(properties, tpHttpClient, teamConverter);
        TeamMcpTools teamMcpTools = new TeamMcpTools(teamSearchService, teamGetByIdService);

        // Wiring TeamIteration
        TeamIterationConverter teamIterationConverter = new TeamIterationConverter();
        TeamIterationSearchService teamIterationSearchService = new TeamIterationSearchService(properties, tpHttpClient, teamIterationConverter);
        TeamIterationGetByIdService teamIterationGetByIdService = new TeamIterationGetByIdService(properties, tpHttpClient, teamIterationConverter);
        TeamIterationMcpTools teamIterationMcpTools = new TeamIterationMcpTools(teamIterationSearchService, teamIterationGetByIdService);

        // Wiring TestCase
        TestCaseConverter testCaseConverter = new TestCaseConverter();
        TestCaseSearchService testCaseSearchService = new TestCaseSearchService(properties, tpHttpClient, testCaseConverter);
        TestCaseCreateService testCaseCreateService = new TestCaseCreateService(properties, tpHttpClient, testCaseConverter, mapper);
        TestCaseUpdateService testCaseUpdateService = new TestCaseUpdateService(properties, tpHttpClient, testCaseConverter, mapper);
        TestCaseGetByIdService testCaseGetByIdService = new TestCaseGetByIdService(properties, tpHttpClient, testCaseConverter);
        TestCaseMcpTools testCaseMcpTools = new TestCaseMcpTools(testCaseSearchService, testCaseCreateService, testCaseUpdateService, testCaseGetByIdService);

        // Wiring TestPlan
        TestPlanConverter testPlanConverter = new TestPlanConverter();
        TestPlanSearchService testPlanSearchService = new TestPlanSearchService(properties, tpHttpClient, testPlanConverter);
        TestPlanCreateService testPlanCreateService = new TestPlanCreateService(properties, tpHttpClient, testPlanConverter, mapper);
        TestPlanUpdateService testPlanUpdateService = new TestPlanUpdateService(properties, tpHttpClient, testPlanConverter, mapper);
        TestPlanGetByIdService testPlanGetByIdService = new TestPlanGetByIdService(properties, tpHttpClient, testPlanConverter);
        TestPlanMcpTools testPlanMcpTools = new TestPlanMcpTools(testPlanSearchService, testPlanCreateService, testPlanUpdateService, testPlanGetByIdService);

        // Wiring UserStory
        UserStoryConverter userStoryConverter = new UserStoryConverter();
        UserStorySearchService userStorySearchService = new UserStorySearchService(properties, tpHttpClient, userStoryConverter);
        UserStoryCreateService userStoryCreateService = new UserStoryCreateService(properties, tpHttpClient, userStoryConverter, mapper);
        UserStoryUpdateService userStoryUpdateService = new UserStoryUpdateService(properties, tpHttpClient, userStoryConverter, mapper);
        UserStoryGetByIdService userStoryGetByIdService = new UserStoryGetByIdService(properties, tpHttpClient, userStoryConverter);
        UserStoryMcpTools userStoryMcpTools = new UserStoryMcpTools(userStorySearchService, userStoryCreateService, userStoryUpdateService, userStoryGetByIdService);

        // Construct server and register
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
