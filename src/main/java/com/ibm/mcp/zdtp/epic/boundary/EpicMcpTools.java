package com.ibm.mcp.zdtp.epic.boundary;

import com.ibm.mcp.zdtp.epic.entity.EpicDto;
import com.ibm.mcp.zdtp.epic.control.*;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class EpicMcpTools {
    private final EpicSearchService searchSvc;
    private final EpicCreateService createSvc;
    private final EpicUpdateService updateSvc;
    private final EpicGetByIdService getSvc;

    private final EpicDeleteService deleteSvc;

    public EpicMcpTools(EpicSearchService s, EpicCreateService c, EpicUpdateService u, EpicGetByIdService g, EpicDeleteService d) {
        this.searchSvc = s; this.createSvc = c; this.updateSvc = u; this.getSvc = g; this.deleteSvc = d;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private EpicSearchService searchSvc;
        private EpicCreateService createSvc;
        private EpicUpdateService updateSvc;
        private EpicGetByIdService getSvc;
        private EpicDeleteService deleteSvc;

        public Builder searchSvc(EpicSearchService s) { this.searchSvc = s; return this; }
        public Builder createSvc(EpicCreateService c) { this.createSvc = c; return this; }
        public Builder updateSvc(EpicUpdateService u) { this.updateSvc = u; return this; }
        public Builder getSvc(EpicGetByIdService g) { this.getSvc = g; return this; }
        public Builder deleteSvc(EpicDeleteService d) { this.deleteSvc = d; return this; }

        public EpicMcpTools build() {
            return new EpicMcpTools(searchSvc, createSvc, updateSvc, getSvc, deleteSvc);
        }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("epic_search", "Search for epics.",
                schema.object().prop("nameQuery", schema.string()).prop("projectName", schema.string()).prop("ownerLogin", schema.string())
                        .prop("startDate", schema.string()).prop("endDate", schema.string()).prop("take", schema.integer().withDefault(10))
                        .prop("skip", schema.integer().withDescription("Number of items to skip for pagination.")).build(),
                args -> search(EpicSearchService.SearchCriteria.builder()
                        .nameQuery(args.path("nameQuery").asText(null))
                        .projectName(args.path("projectName").asText(null))
                        .ownerLogin(args.path("ownerLogin").asText(null))
                        .startDate(args.path("startDate").asText(null))
                        .endDate(args.path("endDate").asText(null))
                        .take(args.path("take").asInt(10))
                        .skip(args.has("skip") ? args.path("skip").asInt() : null)
                        .build()));

        server.registerTool("epic_create", "Create a new epic.",
                schema.object().prop("name", schema.string().required()).prop("projectId", schema.integer().required())
                        .prop("description", schema.string()).prop("effort", schema.number()).build(),
                args -> create(args.path("name").asText(), args.path("projectId").asInt(), args.path("description").asText(null), args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("epic_update", "Update an existing epic.",
                schema.object().prop("id", schema.integer().required()).prop("name", schema.string()).prop("description", schema.string())
                        .prop("stateName", schema.string()).prop("effort", schema.number()).build(),
                args -> update(args.path("id").asInt(), args.path("name").asText(null), args.path("description").asText(null), args.path("stateName").asText(null), args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("epic_get", "Get an epic by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> get(args.path("id").asInt()));

        server.registerTool("epic_delete", "Delete an epic by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> delete(args.path("id").asInt()));
    }

    private String delete(int i) {
        if (deleteSvc != null) deleteSvc.delete(i);
        return "Epic [%d] deleted successfully.".formatted(i);
    }

    private String search(EpicSearchService.SearchCriteria c) {
        var res = searchSvc.search(c);
        return res.isEmpty() ? "No epics found." : String.join("\n", res.stream().map(this::format).toList());
    }

    private String create(String n, int p, String d, Double e) { return "Created: " + format(createSvc.create(n, p, d, e)); }
    private String update(int i, String n, String d, String s, Double e) { return "Updated: " + format(updateSvc.update(i, n, d, s, e)); }
    private String get(int i) { var e = getSvc.get(i); return format(e) + "\nDescription:\n" + (e.description() != null ? e.description() : "N/A"); }

    private String format(EpicDto e) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Points: %s, Created: %s, Done: %s)"
                .formatted(e.id(), e.name(), ns(e.projectName()), ns(e.state()), ns(e.ownerLogin()),
                        e.effort() != null ? e.effort().toString() : "N/A", ns(e.createdAt()), ns(e.endDate()));
    }

    private String ns(String v) { return v != null ? v : "N/A"; }
}

