package com.ibm.mcp.zdtp.user.boundary;

import com.ibm.mcp.zdtp.user.control.UserSearchService;
import com.ibm.mcp.zdtp.user.entity.UserDto;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class UserMcpTools {
    private final UserSearchService searchSvc;

    public UserMcpTools(UserSearchService searchSvc) {
        this.searchSvc = searchSvc;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("user_search", "Search for active users by name, login or email.",
                schema.object()
                        .prop("query", schema.string().required().withDescription("The search term (e.g., 'Aldo' or 'aldo@example.com')."))
                        .prop("take", schema.integer().withDefault(10).withDescription("Max results to return."))
                        .build(),
                args -> search(args.path("query").asText(), args.path("take").asInt(10)));
    }

    private String search(String query, int take) {
        List<UserDto> users = searchSvc.search(query, take);
        if (users.isEmpty()) return "No users found.";
        return String.join("\n", users.stream()
                .map(u -> "[%d] %s %s (%s) - %s".formatted(u.id(), u.firstName(), u.lastName(), u.login(), u.email()))
                .toList());
    }
}
