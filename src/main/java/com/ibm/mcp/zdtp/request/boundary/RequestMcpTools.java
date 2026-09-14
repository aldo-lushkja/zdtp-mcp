package com.ibm.mcp.zdtp.request.boundary;

import com.ibm.mcp.zdtp.request.entity.RequestDto;
import com.ibm.mcp.zdtp.request.control.*;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class RequestMcpTools {
    private final RequestSearchService searchSvc;
    private final RequestCreateService createSvc;
    private final RequestUpdateService updateSvc;
    private final RequestGetByIdService getSvc;

    private final RequestDeleteService deleteSvc;

    public RequestMcpTools(RequestSearchService s, RequestCreateService c, RequestUpdateService u, RequestGetByIdService g, RequestDeleteService d) {
        this.searchSvc = s; this.createSvc = c; this.updateSvc = u; this.getSvc = g; this.deleteSvc = d;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private RequestSearchService searchSvc;
        private RequestCreateService createSvc;
        private RequestUpdateService updateSvc;
        private RequestGetByIdService getSvc;
        private RequestDeleteService deleteSvc;

        public Builder searchSvc(RequestSearchService s) { this.searchSvc = s; return this; }
        public Builder createSvc(RequestCreateService c) { this.createSvc = c; return this; }
        public Builder updateSvc(RequestUpdateService u) { this.updateSvc = u; return this; }
        public Builder getSvc(RequestGetByIdService g) { this.getSvc = g; return this; }
        public Builder deleteSvc(RequestDeleteService d) { this.deleteSvc = d; return this; }

        public RequestMcpTools build() {
            return new RequestMcpTools(searchSvc, createSvc, updateSvc, getSvc, deleteSvc);
        }
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("request_search", "Search for requests.",
                schema.object().prop("nameQuery", schema.string()).prop("projectName", schema.string()).prop("ownerLogin", schema.string())
                        .prop("startDate", schema.string()).prop("endDate", schema.string()).prop("take", schema.integer().withDefault(10))
                        .prop("skip", schema.integer().withDescription("Number of items to skip for pagination.")).build(),
                args -> search(RequestSearchService.SearchCriteria.builder()
                        .nameQuery(args.path("nameQuery").asText(null))
                        .projectName(args.path("projectName").asText(null))
                        .ownerLogin(args.path("ownerLogin").asText(null))
                        .startDate(args.path("startDate").asText(null))
                        .endDate(args.path("endDate").asText(null))
                        .take(args.path("take").asInt(10))
                        .skip(args.has("skip") ? args.path("skip").asInt() : null)
                        .build()));

        server.registerTool("request_create", "Create a new request.",
                schema.object().prop("name", schema.string().required()).prop("projectId", schema.integer().required())
                        .prop("description", schema.string()).prop("effort", schema.number()).build(),
                args -> create(args.path("name").asText(), args.path("projectId").asInt(), args.path("description").asText(null), args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("request_update", "Update an existing request.",
                schema.object().prop("id", schema.integer().required()).prop("name", schema.string()).prop("description", schema.string())
                        .prop("stateName", schema.string()).prop("effort", schema.number()).build(),
                args -> update(args.path("id").asInt(), args.path("name").asText(null), args.path("description").asText(null), args.path("stateName").asText(null), args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("request_get", "Get a request by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> get(args.path("id").asInt()));

        server.registerTool("request_delete", "Delete a request by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> delete(args.path("id").asInt()));
    }

    private String delete(int i) {
        if (deleteSvc != null) deleteSvc.delete(i);
        return "Request [%d] deleted successfully.".formatted(i);
    }

    private String search(RequestSearchService.SearchCriteria c) {
        var res = searchSvc.search(c);
        return res.isEmpty() ? "No requests found." : String.join("\n", res.stream().map(this::format).toList());
    }

    private String create(String n, int p, String d, Double e) { return "Created: " + format(createSvc.create(n, p, d, e)); }
    private String update(int i, String n, String d, String s, Double e) { return "Updated: " + format(updateSvc.update(i, n, d, s, e)); }
    private String get(int i) { var r = getSvc.get(i); return format(r) + "\nDescription:\n" + (r.description() != null ? r.description() : "N/A"); }

    private String format(RequestDto r) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Points: %s, Created: %s, Done: %s)"
                .formatted(r.id(), r.name(), ns(r.projectName()), ns(r.state()), ns(r.ownerLogin()),
                        r.effort() != null ? r.effort().toString() : "N/A", ns(r.createdAt()), ns(r.endDate()));
    }

    private String ns(String v) { return v != null ? v : "N/A"; }
}

