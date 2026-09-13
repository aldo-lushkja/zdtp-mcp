package com.ibm.mcp.zdtp.bug.boundary;

import com.ibm.mcp.zdtp.bug.entity.BugDto;
import com.ibm.mcp.zdtp.bug.control.*;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class BugMcpTools {
    private final BugSearchService searchSvc;
    private final BugCreateService createSvc;
    private final BugUpdateService updateSvc;
    private final BugGetByIdService getSvc;
    private final BugDeleteService deleteSvc;

    public BugMcpTools(BugSearchService s, BugCreateService c, BugUpdateService u, BugGetByIdService g, BugDeleteService d) {
        this.searchSvc = s; this.createSvc = c; this.updateSvc = u; this.getSvc = g; this.deleteSvc = d;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private BugSearchService searchSvc;
        private BugCreateService createSvc;
        private BugUpdateService updateSvc;
        private BugGetByIdService getSvc;
        private BugDeleteService deleteSvc;

        public Builder searchSvc(BugSearchService s) { this.searchSvc = s; return this; }
        public Builder createSvc(BugCreateService c) { this.createSvc = c; return this; }
        public Builder updateSvc(BugUpdateService u) { this.updateSvc = u; return this; }
        public Builder getSvc(BugGetByIdService g) { this.getSvc = g; return this; }
        public Builder deleteSvc(BugDeleteService d) { this.deleteSvc = d; return this; }

        public BugMcpTools build() {
            return new BugMcpTools(searchSvc, createSvc, updateSvc, getSvc, deleteSvc);
        }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("bug_search", "Search for bugs.",
                schema.object().prop("nameQuery", schema.string()).prop("projectName", schema.string()).prop("ownerLogin", schema.string())
                        .prop("startDate", schema.string()).prop("endDate", schema.string()).prop("userStoryId", schema.integer())
                        .prop("featureId", schema.integer()).prop("take", schema.integer().withDefault(10)).build(),
                args -> search(BugSearchService.SearchCriteria.builder()
                        .nameQuery(args.path("nameQuery").asText(null))
                        .projectName(args.path("projectName").asText(null))
                        .ownerLogin(args.path("ownerLogin").asText(null))
                        .startDate(args.path("startDate").asText(null))
                        .endDate(args.path("endDate").asText(null))
                        .take(args.path("take").asInt(10))
                        .userStoryId(args.has("userStoryId") ? args.path("userStoryId").asInt() : null)
                        .featureId(args.has("featureId") ? args.path("featureId").asInt() : null)
                        .build()));

        server.registerTool("bug_create", "Create a new bug.",
                schema.object().prop("name", schema.string().required()).prop("projectId", schema.integer().required())
                        .prop("description", schema.string()).prop("effort", schema.number())
                        .prop("userStoryId", schema.integer()).prop("featureId", schema.integer()).build(),
                args -> create(args.path("name").asText(), args.path("projectId").asInt(), args.path("description").asText(null),
                        args.has("effort") ? args.path("effort").asDouble() : null,
                        args.has("userStoryId") ? args.path("userStoryId").asInt() : null,
                        args.has("featureId") ? args.path("featureId").asInt() : null));

        server.registerTool("bug_update", "Update an existing bug.",
                schema.object().prop("id", schema.integer().required()).prop("name", schema.string()).prop("description", schema.string())
                        .prop("stateName", schema.string()).prop("effort", schema.number()).build(),
                args -> update(args.path("id").asInt(), args.path("name").asText(null), args.path("description").asText(null),
                        args.path("stateName").asText(null), args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("bug_get", "Get a bug by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> get(args.path("id").asInt()));

        server.registerTool("bug_delete", "Delete a bug by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> delete(args.path("id").asInt()));
    }

    private String search(BugSearchService.SearchCriteria c) {
        var res = searchSvc.search(c);
        return res.isEmpty() ? "No bugs found." : String.join("\n", res.stream().map(this::format).toList());
    }

    private String create(String n, int p, String d, Double e, Integer us, Integer f) { return "Created: " + format(createSvc.create(n, p, d, e, us, f)); }
    private String update(int i, String n, String d, String s, Double e) { return "Updated: " + format(updateSvc.update(i, n, d, s, e)); }
    private String get(int i) { var s = getSvc.get(i); return format(s) + "\nDescription:\n" + (s.description() != null ? s.description() : "N/A"); }

    private String delete(int i) {
        deleteSvc.delete(i);
        return "Bug [%d] deleted successfully.".formatted(i);
    }

    private String format(BugDto s) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Assignee: %s, Points: %s, Created: %s, Done: %s, UserStory: %s, Feature: %s, Sprint: %s)"
                .formatted(s.id(), s.name(), ns(s.projectName()), ns(s.state()), ns(s.ownerLogin()), ns(s.assigneeLogin()),
                        s.effort() != null ? s.effort() : 0.0, ns(s.createdAt()), ns(s.endDate()),
                        s.userStoryName() != null ? s.userStoryId() + " " + s.userStoryName() : "N/A",
                        s.featureName() != null ? s.featureId() + " " + s.featureName() : "N/A",
                        s.sprintName() != null ? s.sprintId() + " " + s.sprintName() : "N/A");
    }

    private String ns(String v) { return v != null ? v : "N/A"; }
}
