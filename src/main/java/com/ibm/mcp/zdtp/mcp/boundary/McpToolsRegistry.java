package com.ibm.mcp.zdtp.mcp.boundary;

import com.ibm.mcp.zdtp.bug.boundary.BugMcpTools;
import com.ibm.mcp.zdtp.bug.control.*;
import com.ibm.mcp.zdtp.comment.boundary.CommentMcpTools;
import com.ibm.mcp.zdtp.comment.control.CommentConverter;
import com.ibm.mcp.zdtp.comment.control.CommentCreateService;
import com.ibm.mcp.zdtp.epic.boundary.EpicMcpTools;
import com.ibm.mcp.zdtp.epic.control.*;
import com.ibm.mcp.zdtp.feature.boundary.FeatureMcpTools;
import com.ibm.mcp.zdtp.feature.control.*;
import com.ibm.mcp.zdtp.impediment.boundary.ImpedimentMcpTools;
import com.ibm.mcp.zdtp.impediment.control.ImpedimentConverter;
import com.ibm.mcp.zdtp.impediment.control.ImpedimentCreateService;
import com.ibm.mcp.zdtp.impediment.control.ImpedimentSearchService;
import com.ibm.mcp.zdtp.project.boundary.ProjectMcpTools;
import com.ibm.mcp.zdtp.project.control.ProjectConverter;
import com.ibm.mcp.zdtp.project.control.ProjectSearchService;
import com.ibm.mcp.zdtp.relation.boundary.RelationMcpTools;
import com.ibm.mcp.zdtp.relation.control.RelationConverter;
import com.ibm.mcp.zdtp.relation.control.RelationCreateService;
import com.ibm.mcp.zdtp.relation.control.RelationSearchService;
import com.ibm.mcp.zdtp.release.boundary.ReleaseMcpTools;
import com.ibm.mcp.zdtp.release.control.*;
import com.ibm.mcp.zdtp.request.boundary.RequestMcpTools;
import com.ibm.mcp.zdtp.request.control.*;
import com.ibm.mcp.zdtp.shared.control.EntityStateSearchService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.task.boundary.TaskMcpTools;
import com.ibm.mcp.zdtp.task.control.*;
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
import com.ibm.mcp.zdtp.time.boundary.TimeMcpTools;
import com.ibm.mcp.zdtp.time.control.TimeConverter;
import com.ibm.mcp.zdtp.time.control.TimeLogService;
import com.ibm.mcp.zdtp.user.boundary.UserMcpTools;
import com.ibm.mcp.zdtp.user.control.UserConverter;
import com.ibm.mcp.zdtp.user.control.UserSearchService;
import com.ibm.mcp.zdtp.userstory.boundary.UserStoryMcpTools;
import com.ibm.mcp.zdtp.userstory.control.*;

/**
 * Encapsulates registration of all MCP domain tools using modular helper methods (max 5 lines per method).
 */
public class McpToolsRegistry {

    private final QueryEngine engine;

    public McpToolsRegistry(QueryEngine engine) {
        this.engine = engine;
    }

    public void registerAllTools(McpServer server, SchemaBuilder schema) {
        registerWorkItemTools(server, schema);
        registerQualityTools(server, schema);
        registerPlanningTools(server, schema);
        registerSupportTools(server, schema);
    }

    private void registerWorkItemTools(McpServer server, SchemaBuilder schema) {
        registerEpicTools(server, schema);
        registerFeatureTools(server, schema);
        registerUserStoryTools(server, schema);
        registerBugTools(server, schema);
        registerTaskTools(server, schema);
    }

    private void registerQualityTools(McpServer server, SchemaBuilder schema) {
        registerTestCaseTools(server, schema);
        registerTestPlanTools(server, schema);
    }

    private void registerPlanningTools(McpServer server, SchemaBuilder schema) {
        registerProjectTools(server, schema);
        registerReleaseTools(server, schema);
        registerTeamTools(server, schema);
        registerTeamIterationTools(server, schema);
    }

    private void registerSupportTools(McpServer server, SchemaBuilder schema) {
        registerRequestTools(server, schema);
        registerCommentTools(server, schema);
        registerUserTools(server, schema);
        registerRelationTools(server, schema);
        registerTimeTools(server, schema);
        registerImpedimentTools(server, schema);
    }

    private void registerEpicTools(McpServer server, SchemaBuilder schema) {
        var c = new EpicConverter();
        EpicMcpTools.builder()
                .searchSvc(new EpicSearchService(engine, c)).createSvc(new EpicCreateService(engine, c))
                .updateSvc(new EpicUpdateService(engine, c)).getSvc(new EpicGetByIdService(engine, c))
                .deleteSvc(new EpicDeleteService(engine)).build().register(server, schema);
    }

    private void registerFeatureTools(McpServer server, SchemaBuilder schema) {
        var c = new FeatureConverter();
        FeatureMcpTools.builder()
                .searchSvc(new FeatureSearchService(engine, c)).createSvc(new FeatureCreateService(engine, c))
                .updateSvc(new FeatureUpdateService(engine, c)).getSvc(new FeatureGetByIdService(engine, c))
                .deleteSvc(new FeatureDeleteService(engine)).build().register(server, schema);
    }

    private void registerUserStoryTools(McpServer server, SchemaBuilder schema) {
        var c = new UserStoryConverter();
        UserStoryMcpTools.builder()
                .searchService(new UserStorySearchService(engine, c)).createService(new UserStoryCreateService(engine, c))
                .updateService(new UserStoryUpdateService(engine, c)).getByIdService(new UserStoryGetByIdService(engine, c))
                .deleteService(new UserStoryDeleteService(engine)).build().register(server, schema);
    }

    private void registerBugTools(McpServer server, SchemaBuilder schema) {
        var c = new BugConverter();
        BugMcpTools.builder()
                .searchSvc(new BugSearchService(engine, c)).createSvc(new BugCreateService(engine, c))
                .updateSvc(new BugUpdateService(engine, c)).getSvc(new BugGetByIdService(engine, c))
                .deleteSvc(new BugDeleteService(engine)).build().register(server, schema);
    }

    private void registerTaskTools(McpServer server, SchemaBuilder schema) {
        var c = new TaskConverter();
        TaskMcpTools.builder()
                .searchSvc(new TaskSearchService(engine, c)).createSvc(new TaskCreateService(engine, c))
                .updateSvc(new TaskUpdateService(engine, c)).getSvc(new TaskGetByIdService(engine, c))
                .deleteSvc(new TaskDeleteService(engine)).build().register(server, schema);
    }

    private void registerTestCaseTools(McpServer server, SchemaBuilder schema) {
        var tc = new TestCaseConverter();
        var ts = new TestStepConverter();
        TestCaseMcpTools.builder()
                .searchSvc(new TestCaseSearchService(engine, tc)).createSvc(new TestCaseCreateService(engine, tc))
                .updateSvc(new TestCaseUpdateService(engine, tc)).getSvc(new TestCaseGetByIdService(engine, tc))
                .stepCreateSvc(new TestStepCreateService(engine, ts)).deleteSvc(new TestCaseDeleteService(engine))
                .stepDeleteSvc(new TestStepDeleteService(engine)).build().register(server, schema);
    }

    private void registerTestPlanTools(McpServer server, SchemaBuilder schema) {
        var c = new TestPlanConverter();
        TestPlanMcpTools.builder()
                .searchSvc(new TestPlanSearchService(engine, c)).createSvc(new TestPlanCreateService(engine, c))
                .updateSvc(new TestPlanUpdateService(engine, c)).getSvc(new TestPlanGetByIdService(engine, c))
                .deleteSvc(new TestPlanDeleteService(engine)).build().register(server, schema);
    }

    private void registerProjectTools(McpServer server, SchemaBuilder schema) {
        var c = new ProjectConverter();
        ProjectMcpTools.builder()
                .searchSvc(new ProjectSearchService(engine, c))
                .stateSvc(new EntityStateSearchService(engine))
                .build().register(server, schema);
    }

    private void registerReleaseTools(McpServer server, SchemaBuilder schema) {
        var c = new ReleaseConverter();
        ReleaseMcpTools.builder()
                .searchSvc(new ReleaseSearchService(engine, c)).createSvc(new ReleaseCreateService(engine, c))
                .updateSvc(new ReleaseUpdateService(engine, c)).getSvc(new ReleaseGetByIdService(engine, c))
                .deleteSvc(new ReleaseDeleteService(engine)).build().register(server, schema);
    }

    private void registerTeamTools(McpServer server, SchemaBuilder schema) {
        var c = new TeamConverter();
        TeamMcpTools.builder()
                .searchService(new TeamSearchService(engine, c)).getService(new TeamGetByIdService(engine, c))
                .build().register(server, schema);
    }

    private void registerTeamIterationTools(McpServer server, SchemaBuilder schema) {
        var c = new TeamIterationConverter();
        TeamIterationMcpTools.builder()
                .searchService(new TeamIterationSearchService(engine, c)).getService(new TeamIterationGetByIdService(engine, c))
                .build().register(server, schema);
    }

    private void registerRequestTools(McpServer server, SchemaBuilder schema) {
        var c = new RequestConverter();
        RequestMcpTools.builder()
                .searchSvc(new RequestSearchService(engine, c)).createSvc(new RequestCreateService(engine, c))
                .updateSvc(new RequestUpdateService(engine, c)).getSvc(new RequestGetByIdService(engine, c))
                .deleteSvc(new RequestDeleteService(engine)).build().register(server, schema);
    }

    private void registerCommentTools(McpServer server, SchemaBuilder schema) {
        var c = new CommentConverter();
        CommentMcpTools.builder().createService(new CommentCreateService(engine, c)).build().register(server, schema);
    }

    private void registerUserTools(McpServer server, SchemaBuilder schema) {
        var c = new UserConverter();
        UserMcpTools.builder().searchSvc(new UserSearchService(engine, c)).build().register(server, schema);
    }

    private void registerRelationTools(McpServer server, SchemaBuilder schema) {
        var c = new RelationConverter();
        RelationMcpTools.builder()
                .searchSvc(new RelationSearchService(engine, c)).createSvc(new RelationCreateService(engine, c))
                .build().register(server, schema);
    }

    private void registerTimeTools(McpServer server, SchemaBuilder schema) {
        var c = new TimeConverter();
        TimeMcpTools.builder().logService(new TimeLogService(engine, c)).build().register(server, schema);
    }

    private void registerImpedimentTools(McpServer server, SchemaBuilder schema) {
        var c = new ImpedimentConverter();
        ImpedimentMcpTools.builder()
                .createSvc(new ImpedimentCreateService(engine, c))
                .searchSvc(new ImpedimentSearchService(engine, c))
                .build().register(server, schema);
    }
}
