package com.ibm.mcp.zdtp.release.boundary;

import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.release.control.*;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class ReleaseMcpTools {
    private final ReleaseSearchService searchSvc;
    private final ReleaseCreateService createSvc;
    private final ReleaseUpdateService updateSvc;
    private final ReleaseGetByIdService getSvc;

    public ReleaseMcpTools(ReleaseSearchService s, ReleaseCreateService c, ReleaseUpdateService u, ReleaseGetByIdService g) {
        this.searchSvc = s; this.createSvc = c; this.updateSvc = u; this.getSvc = g;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private ReleaseSearchService searchSvc;
        private ReleaseCreateService createSvc;
        private ReleaseUpdateService updateSvc;
        private ReleaseGetByIdService getSvc;

        public Builder searchSvc(ReleaseSearchService s) { this.searchSvc = s; return this; }
        public Builder createSvc(ReleaseCreateService c) { this.createSvc = c; return this; }
        public Builder updateSvc(ReleaseUpdateService u) { this.updateSvc = u; return this; }
        public Builder getSvc(ReleaseGetByIdService g) { this.getSvc = g; return this; }

        public ReleaseMcpTools build() {
            return new ReleaseMcpTools(searchSvc, createSvc, updateSvc, getSvc);
        }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("release_search", "Search for releases.",
                schema.object().prop("nameQuery", schema.string()).prop("projectName", schema.string()).prop("ownerLogin", schema.string())
                        .prop("startDate", schema.string()).prop("endDate", schema.string()).prop("teamIterationId", schema.integer())
                        .prop("take", schema.integer().withDefault(10)).build(),
                args -> search(ReleaseSearchService.SearchCriteria.builder()
                        .nameQuery(args.path("nameQuery").asText(null))
                        .projectName(args.path("projectName").asText(null))
                        .ownerLogin(args.path("ownerLogin").asText(null))
                        .startDate(args.path("startDate").asText(null))
                        .endDate(args.path("endDate").asText(null))
                        .take(args.path("take").asInt(10))
                        .teamIterationId(args.has("teamIterationId") ? args.path("teamIterationId").asInt() : null)
                        .build()));

        server.registerTool("release_create", "Create a new release.",
                schema.object().prop("name", schema.string().required()).prop("projectId", schema.integer().required())
                        .prop("description", schema.string()).prop("effort", schema.number()).build(),
                args -> create(args.path("name").asText(), args.path("projectId").asInt(), args.path("description").asText(null), args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("release_update", "Update an existing release.",
                schema.object().prop("id", schema.integer().required()).prop("name", schema.string()).prop("description", schema.string())
                        .prop("stateName", schema.string()).prop("effort", schema.number()).build(),
                args -> update(args.path("id").asInt(), args.path("name").asText(null), args.path("description").asText(null), args.path("stateName").asText(null), args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("release_get", "Get a release by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> get(args.path("id").asInt()));
    }

    private String search(ReleaseSearchService.SearchCriteria c) {
        var res = searchSvc.search(c);
        return res.isEmpty() ? "No releases found." : String.join("\n", res.stream().map(this::format).toList());
    }

    private String create(String n, int p, String d, Double e) { return "Created: " + format(createSvc.create(n, p, d, e)); }
    private String update(int i, String n, String d, String s, Double e) { return "Updated: " + format(updateSvc.update(i, n, d, s, e)); }
    private String get(int i) { var r = getSvc.get(i); return format(r) + "\nDescription:\n" + (r.description() != null ? r.description() : "N/A"); }

    private String format(ReleaseDto r) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Points: %s, Created: %s, Start: %s, End: %s)"
                .formatted(r.id(), r.name(), ns(r.projectName()), ns(r.state()), ns(r.ownerLogin()),
                        r.effort() != null ? r.effort().toString() : "N/A", ns(r.createdAt()), ns(r.startDate()), ns(r.endDate()));
    }

    private String ns(String v) { return v != null ? v : "N/A"; }
}

