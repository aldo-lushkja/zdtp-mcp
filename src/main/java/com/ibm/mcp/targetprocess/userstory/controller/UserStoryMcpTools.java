package com.ibm.mcp.targetprocess.userstory.controller;

import com.ibm.mcp.targetprocess.userstory.dto.UserStoryDto;
import com.ibm.mcp.targetprocess.userstory.service.UserStoryCreateService;
import com.ibm.mcp.targetprocess.userstory.service.UserStoryGetByIdService;
import com.ibm.mcp.targetprocess.userstory.service.UserStorySearchService;
import com.ibm.mcp.targetprocess.userstory.service.UserStoryUpdateService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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

    @Tool(description = """
            Search for user stories in Targetprocess. \
            Supports filtering by story name, project name, owner login, \
            creation date range (YYYY-MM-DD), and releaseId (numeric release ID). \
            Results are ordered by creation date descending.""")
    public String searchUserStories(String nameQuery, String projectName,
                                    String creatorLogin, String startDate,
                                    String endDate, int take, Integer releaseId) {
        List<UserStoryDto> stories = userStorySearchService.searchUserStories(
                nameQuery, projectName, creatorLogin, startDate, endDate, take, releaseId);

        if (stories.isEmpty()) {
            return "No user stories found.";
        }

        return String.join("\n", stories.stream().map(this::format).toList());
    }

    @Tool(description = """
            Create a new user story in Targetprocess. \
            Requires name and projectId (numeric ID of the project). \
            Description and effort (story points) are optional. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String createUserStory(String name, int projectId, String description, Double effort) {
        UserStoryDto story = userStoryCreateService.createUserStory(name, projectId, description, effort);
        return "Created: " + format(story);
    }

    @Tool(description = """
            Update an existing user story in Targetprocess by its numeric ID. \
            All fields except id are optional — only provided (non-blank) fields are updated. \
            stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String updateUserStory(int id, String name, String description, String stateName, Double effort) {
        UserStoryDto story = userStoryUpdateService.updateUserStory(id, name, description, stateName, effort);
        return "Updated: " + format(story);
    }

    @Tool(description = "Get a user story by its numeric ID. Returns full details including description.")
    public String getUserStoryById(int id) {
        UserStoryDto story = userStoryGetByIdService.getById(id);
        return format(story) + "\nDescription:\n" + nullSafe(story.description());
    }

    private String format(UserStoryDto s) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Assignee: %s, Points: %s, Created: %s, Done: %s, Release: %s)"
            .formatted(
                s.id(), s.name(),
                nullSafe(s.projectName()), nullSafe(s.state()),
                nullSafe(s.ownerLogin()), nullSafe(s.assigneeLogin()),
                s.effort() != null ? s.effort().toString() : "N/A",
                nullSafe(s.createdAt()), nullSafe(s.endDate()),
                s.releaseName() != null ? s.releaseId() + " " + s.releaseName() : "N/A"
            );
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}
