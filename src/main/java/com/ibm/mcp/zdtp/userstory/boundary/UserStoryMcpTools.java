package com.ibm.mcp.zdtp.userstory.boundary;

import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;
import com.ibm.mcp.zdtp.userstory.control.*;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class UserStoryMcpTools {
    private final UserStorySearchService searchService;
    private final UserStoryCreateService createService;
    private final UserStoryUpdateService updateService;
    private final UserStoryGetByIdService getByIdService;
    private final UserStoryDeleteService deleteService;

    public UserStoryMcpTools(UserStorySearchService s, UserStoryCreateService c, UserStoryUpdateService u, UserStoryGetByIdService g, UserStoryDeleteService d) {
        this.searchService = s; this.createService = c; this.updateService = u; this.getByIdService = g; this.deleteService = d;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UserStorySearchService searchService;
        private UserStoryCreateService createService;
        private UserStoryUpdateService updateService;
        private UserStoryGetByIdService getByIdService;
        private UserStoryDeleteService deleteService;

        public Builder searchService(UserStorySearchService s) { this.searchService = s; return this; }
        public Builder createService(UserStoryCreateService c) { this.createService = c; return this; }
        public Builder updateService(UserStoryUpdateService u) { this.updateService = u; return this; }
        public Builder getByIdService(UserStoryGetByIdService g) { this.getByIdService = g; return this; }
        public Builder deleteService(UserStoryDeleteService d) { this.deleteService = d; return this; }

        public UserStoryMcpTools build() {
            return new UserStoryMcpTools(searchService, createService, updateService, getByIdService, deleteService);
        }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("user_story_search", "Search for user stories in Targetprocess.",
                schema.object().prop("nameQuery", schema.string()).prop("projectName", schema.string()).prop("ownerLogin", schema.string())
                        .prop("startDate", schema.string()).prop("endDate", schema.string()).prop("releaseId", schema.integer())
                        .prop("sprintId", schema.integer()).prop("take", schema.integer().withDefault(10)).build(),
                args -> search(UserStorySearchService.SearchCriteria.builder()
                        .nameQuery(args.path("nameQuery").asText(null))
                        .projectName(args.path("projectName").asText(null))
                        .ownerLogin(args.path("ownerLogin").asText(null))
                        .startDate(args.path("startDate").asText(null))
                        .endDate(args.path("endDate").asText(null))
                        .take(args.path("take").asInt(10))
                        .releaseId(args.has("releaseId") ? args.path("releaseId").asInt() : null)
                        .sprintId(args.has("sprintId") ? args.path("sprintId").asInt() : null)
                        .build()));

        server.registerTool("user_story_create", "Create a new user story.",
                schema.object().prop("name", schema.string().required()).prop("projectId", schema.integer().required())
                        .prop("description", schema.string()).prop("effort", schema.number()).build(),
                args -> create(args.path("name").asText(), args.path("projectId").asInt(), args.path("description").asText(null), args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("user_story_update", "Update an existing user story.",
                schema.object().prop("id", schema.integer().required()).prop("name", schema.string()).prop("description", schema.string())
                        .prop("stateName", schema.string()).prop("effort", schema.number()).build(),
                args -> update(args.path("id").asInt(), args.path("name").asText(null), args.path("description").asText(null), args.path("stateName").asText(null), args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("user_story_get", "Get a user story by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> get(args.path("id").asInt()));

        server.registerTool("user_story_delete", "Delete a user story by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> delete(args.path("id").asInt()));
    }

    private String search(UserStorySearchService.SearchCriteria c) {
        var stories = searchService.search(c);
        return stories.isEmpty() ? "No user stories found." : String.join("\n", stories.stream().map(this::format).toList());
    }

    private String create(String n, int p, String d, Double e) { return "Created: " + format(createService.create(n, p, d, e)); }
    private String update(int i, String n, String d, String s, Double e) { return "Updated: " + format(updateService.update(i, n, d, s, e)); }
    private String get(int i) { var s = getByIdService.get(i); return format(s) + "\nDescription:\n" + (s.description() != null ? s.description() : "N/A"); }

    private String delete(int i) {
        deleteService.delete(i);
        return "User story [%d] deleted successfully.".formatted(i);
    }

    private String format(UserStoryDto s) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Assignee: %s, Points: %s, Created: %s, Done: %s, Release: %s, Sprint: %s)"
                .formatted(s.id(), s.name(), ns(s.projectName()), ns(s.state()), ns(s.ownerLogin()), ns(s.assigneeLogin()),
                        s.effort() != null ? s.effort().toString() : "N/A", ns(s.createdAt()), ns(s.endDate()),
                        s.releaseName() != null ? s.releaseId() + " " + s.releaseName() : "N/A",
                        s.sprintName() != null ? s.sprintId() + " " + s.sprintName() : "N/A");
    }

    private String ns(String v) { return v != null ? v : "N/A"; }
}

