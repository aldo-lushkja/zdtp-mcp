package com.ibm.mcp.targetprocess.userstory.controller;

import com.ibm.mcp.targetprocess.userstory.dto.UserStoryDto;
import com.ibm.mcp.targetprocess.userstory.service.UserStorySearchService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserStoryMcpTools {

    private final UserStorySearchService userStorySearchService;

    public UserStoryMcpTools(UserStorySearchService userStorySearchService) {
        this.userStorySearchService = userStorySearchService;
    }

    @Tool(description = """
            Search for user stories in Targetprocess. \
            Supports filtering by story name, project name, owner login, \
            and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.""")
    public String searchUserStories(String nameQuery, String projectName,
                                    String creatorLogin, String startDate,
                                    String endDate, int take) {
        List<UserStoryDto> stories = userStorySearchService.searchUserStories(
                nameQuery, projectName, creatorLogin, startDate, endDate, take);

        if (stories.isEmpty()) {
            return "No user stories found.";
        }

        return String.join("\n", stories.stream().map(this::format).toList());
    }

    private String format(UserStoryDto s) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Assignee: %s, Points: %s, Created: %s, Done: %s)"
            .formatted(
                s.id(), s.name(),
                nullSafe(s.projectName()), nullSafe(s.state()),
                nullSafe(s.ownerLogin()), nullSafe(s.assigneeLogin()),
                s.effort() != null ? s.effort().toString() : "N/A",
                nullSafe(s.createdAt()), nullSafe(s.endDate())
            );
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}
