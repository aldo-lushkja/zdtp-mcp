package com.ibm.mcp.targetprocess.controller;

import com.ibm.mcp.targetprocess.model.UserStory;
import com.ibm.mcp.targetprocess.service.TargetprocessService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TargetprocessMcpTools {

    private final TargetprocessService targetprocessService;

    public TargetprocessMcpTools(TargetprocessService targetprocessService) {
        this.targetprocessService = targetprocessService;
    }

    @Tool(description = "Search for user stories in Targetprocess by name")
    public String searchUserStories(String query) {
        List<UserStory> stories = targetprocessService.searchUserStories(query);
        if (stories.isEmpty()) {
            return "No user stories found matching query: " + query;
        }

        return stories.stream()
                .map(s -> String.format("[%d] %s (Project: %s, State: %s)",
                        s.id(),
                        s.name(),
                        s.project().name(),
                        s.state().name()))
                .collect(Collectors.joining("\n"));
    }
}
