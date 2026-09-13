package com.ibm.mcp.zdtp.task.boundary;

import com.ibm.mcp.zdtp.task.entity.TaskDto;
import com.ibm.mcp.zdtp.task.control.*;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class TaskMcpTools {
    private final TaskSearchService searchSvc;
    private final TaskCreateService createSvc;
    private final TaskUpdateService updateSvc;
    private final TaskGetByIdService getSvc;
    private final TaskDeleteService deleteSvc;

    public TaskMcpTools(TaskSearchService s, TaskCreateService c, TaskUpdateService u, TaskGetByIdService g, TaskDeleteService d) {
        this.searchSvc = s; this.createSvc = c; this.updateSvc = u; this.getSvc = g; this.deleteSvc = d;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private TaskSearchService searchSvc;
        private TaskCreateService createSvc;
        private TaskUpdateService updateSvc;
        private TaskGetByIdService getSvc;
        private TaskDeleteService deleteSvc;

        public Builder searchSvc(TaskSearchService s) { this.searchSvc = s; return this; }
        public Builder createSvc(TaskCreateService c) { this.createSvc = c; return this; }
        public Builder updateSvc(TaskUpdateService u) { this.updateSvc = u; return this; }
        public Builder getSvc(TaskGetByIdService g) { this.getSvc = g; return this; }
        public Builder deleteSvc(TaskDeleteService d) { this.deleteSvc = d; return this; }

        public TaskMcpTools build() {
            return new TaskMcpTools(searchSvc, createSvc, updateSvc, getSvc, deleteSvc);
        }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("task_search", "Search for tasks.",
                schema.object().prop("nameQuery", schema.string()).prop("projectName", schema.string()).prop("ownerLogin", schema.string())
                        .prop("startDate", schema.string()).prop("endDate", schema.string()).prop("userStoryId", schema.integer())
                        .prop("take", schema.integer().withDefault(10)).build(),
                args -> search(TaskSearchService.SearchCriteria.builder()
                        .nameQuery(args.path("nameQuery").asText(null))
                        .projectName(args.path("projectName").asText(null))
                        .ownerLogin(args.path("ownerLogin").asText(null))
                        .startDate(args.path("startDate").asText(null))
                        .endDate(args.path("endDate").asText(null))
                        .take(args.path("take").asInt(10))
                        .userStoryId(args.has("userStoryId") ? args.path("userStoryId").asInt() : null)
                        .build()));

        server.registerTool("task_create", "Create a new task.",
                schema.object().prop("name", schema.string().required()).prop("projectId", schema.integer().required())
                        .prop("description", schema.string()).prop("userStoryId", schema.integer().required()).build(),
                args -> create(args.path("name").asText(), args.path("projectId").asInt(), args.path("description").asText(null), args.path("userStoryId").asInt()));

        server.registerTool("task_update", "Update an existing task.",
                schema.object().prop("id", schema.integer().required()).prop("name", schema.string()).prop("description", schema.string())
                        .prop("stateName", schema.string()).build(),
                args -> update(args.path("id").asInt(), args.path("name").asText(null), args.path("description").asText(null), args.path("stateName").asText(null)));

        server.registerTool("task_get", "Get a task by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> get(args.path("id").asInt()));

        server.registerTool("task_delete", "Delete a task by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> delete(args.path("id").asInt()));
    }

    private String search(TaskSearchService.SearchCriteria c) {
        var res = searchSvc.search(c);
        return res.isEmpty() ? "No tasks found." : String.join("\n", res.stream().map(this::format).toList());
    }

    private String create(String n, int p, String d, int us) { return "Created: " + format(createSvc.create(n, p, d, us)); }
    private String update(int i, String n, String d, String s) { return "Updated: " + format(updateSvc.update(i, n, d, s)); }
    private String get(int i) { var t = getSvc.get(i); return format(t) + "\nDescription:\n" + (t.description() != null ? t.description() : "N/A"); }

    private String delete(int i) {
        deleteSvc.delete(i);
        return "Task [%d] deleted successfully.".formatted(i);
    }

    private String format(TaskDto t) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Created: %s, UserStory: %s)"
                .formatted(t.id(), t.name(), ns(t.projectName()), ns(t.state()), ns(t.ownerLogin()), ns(t.createdAt()),
                        t.userStoryName() != null ? t.userStoryId() + " " + t.userStoryName() : "N/A");
    }

    private String ns(String v) { return v != null ? v : "N/A"; }
}
