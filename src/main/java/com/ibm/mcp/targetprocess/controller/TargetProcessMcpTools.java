package com.ibm.mcp.targetprocess.controller;

import com.ibm.mcp.targetprocess.dto.UserStoryDto;
import com.ibm.mcp.targetprocess.service.TargetProcessService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TargetProcessMcpTools {

    private final TargetProcessService targetprocessService;

    public TargetProcessMcpTools(TargetProcessService targetprocessService) {
        this.targetprocessService = targetprocessService;
    }

    @Tool(description = """
            Search for user stories in Targetprocess. \
            Supports filtering by story name, project name, owner login, \
            and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.""")
    public String searchUserStories(String nameQuery, String projectName,
                                    String creatorLogin, String startDate,
                                    String endDate, int take) {
        List<UserStoryDto> stories = targetprocessService.searchUserStories(
                nameQuery, projectName, creatorLogin, startDate, endDate, take);

        if (stories.isEmpty()) {
            return "No user stories found.";
        }

        return String.join("\n", stories.stream().map(this::format).toList());
    }

    private String format(UserStoryDto s) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Created: %s)".formatted(
                s.id(), s.name(),
                Optional.ofNullable(s.projectName()).orElse("N/A"),
                Optional.ofNullable(s.state()).orElse("N/A"),
                Optional.ofNullable(s.ownerLogin()).orElse("N/A"),
                Optional.ofNullable(s.createdAt()).orElse("N/A"));
    }
}
