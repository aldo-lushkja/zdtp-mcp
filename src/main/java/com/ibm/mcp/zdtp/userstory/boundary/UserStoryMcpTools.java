package com.ibm.mcp.zdtp.userstory.boundary;

import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;
import com.ibm.mcp.zdtp.userstory.control.UserStoryCreateService;
import com.ibm.mcp.zdtp.userstory.control.UserStoryGetByIdService;
import com.ibm.mcp.zdtp.userstory.control.UserStorySearchService;
import com.ibm.mcp.zdtp.userstory.control.UserStoryUpdateService;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class UserStoryMcpTools {

    private final UserStorySearchService userStorySearchService;
    private final UserStoryCreateService userStoryCreateService;
    private final UserStoryUpdateService userStoryUpdateService;
    private final UserStoryGetByIdService userStoryGetByIdService;

    public UserStoryMcpTools(UserStorySearchService userStorySearchService,
            UserStoryCreateService userStoryCreateService,
            UserStoryUpdateService userStoryUpdateService,
            UserStoryGetByIdService userStoryGetByIdService) {
        this.userStorySearchService = userStorySearchService;
        this.userStoryCreateService = userStoryCreateService;
        this.userStoryUpdateService = userStoryUpdateService;
        this.userStoryGetByIdService = userStoryGetByIdService;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("user_story_search",
                "Search for user stories in Targetprocess. Supports filtering by story name, project name, creator login, creation date range (YYYY-MM-DD), releaseId (numeric release ID), and sprintId (numeric team iteration ID). Results are ordered by creation date descending.",
                schema.object()
                        .prop("nameQuery", schema.string())
                        .prop("projectName", schema.string())
                        .prop("creatorLogin", schema.string())
                        .prop("startDate", schema.string())
                        .prop("endDate", schema.string())
                        .prop("releaseId", schema.integer())
                        .prop("sprintId", schema.integer())
                        .prop("take", schema.integer().withDefault(10))
                        .build(),
                args -> searchUserStories(
                        args.path("nameQuery").asText(null),
                        args.path("projectName").asText(null),
                        args.path("creatorLogin").asText(null),
                        args.path("startDate").asText(null),
                        args.path("endDate").asText(null),
                        args.path("take").asInt(10),
                        args.has("releaseId") ? args.path("releaseId").asInt() : null,
                        args.has("sprintId") ? args.path("sprintId").asInt() : null));

        server.registerTool("user_story_create",
                "Create a new user story in Targetprocess. Requires name and projectId (numeric ID of the project). Description and effort (story points) are optional. IMPORTANT — description formatting rules: (1) Always use HTML, never plain markdown. (2) To embed a diagram, encode the Mermaid definition in base64 and use: <img src=\"https://mermaid.ink/img/<base64>\" alt=\"diagram description\" />",
                schema.object()
                        .prop("name", schema.string().required())
                        .prop("projectId", schema.integer().required())
                        .prop("description", schema.string())
                        .prop("effort", schema.number())
                        .build(),
                args -> createUserStory(
                        args.path("name").asText(),
                        args.path("projectId").asInt(),
                        args.path("description").asText(null),
                        args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("user_story_update",
                "Update an existing user story in Targetprocess by its numeric ID. All fields except id are optional — only provided (non-blank) fields are updated. stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'.",
                schema.object()
                        .prop("id", schema.integer().required())
                        .prop("name", schema.string())
                        .prop("description", schema.string())
                        .prop("stateName", schema.string())
                        .prop("effort", schema.number())
                        .build(),
                args -> updateUserStory(
                        args.path("id").asInt(),
                        args.path("name").asText(null),
                        args.path("description").asText(null),
                        args.path("stateName").asText(null),
                        args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("user_story_get",
                "Get a user story by its numeric ID. Returns full details including description.",
                schema.object()
                        .prop("id", schema.integer().required())
                        .build(),
                args -> getUserStoryById(args.path("id").asInt()));
    }

    public String searchUserStories(String nameQuery, String projectName,
            String creatorLogin, String startDate,
            String endDate, int take, Integer releaseId, Integer sprintId) {
        List<UserStoryDto> stories = userStorySearchService.searchUserStories(
                nameQuery, projectName, creatorLogin, startDate, endDate, take, releaseId, sprintId);

        if (stories.isEmpty()) {
            return "No user stories found.";
        }

        return String.join("\n", stories.stream().map(this::format).toList());
    }

    public String createUserStory(String name, int projectId, String description, Double effort) {
        UserStoryDto story = userStoryCreateService.createUserStory(name, projectId, description, effort);
        return "Created: " + format(story);
    }

    public String updateUserStory(int id, String name, String description, String stateName, Double effort) {
        UserStoryDto story = userStoryUpdateService.updateUserStory(id, name, description, stateName, effort);
        return "Updated: " + format(story);
    }

    public String getUserStoryById(int id) {
        UserStoryDto story = userStoryGetByIdService.getById(id);
        return format(story) + "\nDescription:\n" + nullSafe(story.description());
    }

    private String format(UserStoryDto s) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Assignee: %s, Points: %s, Created: %s, Done: %s, Release: %s, Sprint: %s)"
                .formatted(
                        s.id(), s.name(),
                        nullSafe(s.projectName()), nullSafe(s.state()),
                        nullSafe(s.ownerLogin()), nullSafe(s.assigneeLogin()),
                        s.effort() != null ? s.effort().toString() : "N/A",
                        nullSafe(s.createdAt()), nullSafe(s.endDate()),
                        s.releaseName() != null ? s.releaseId() + " " + s.releaseName() : "N/A",
                        s.sprintName() != null ? s.sprintId() + " " + s.sprintName() : "N/A");
    }

    private String nullSafe(String v) {
        return v != null ? v : "N/A";
    }
}
