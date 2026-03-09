package com.ibm.mcp.zdtp.feature.boundary;

import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.feature.control.*;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

public class FeatureMcpTools {
    private final FeatureSearchService searchSvc;
    private final FeatureCreateService createSvc;
    private final FeatureUpdateService updateSvc;
    private final FeatureGetByIdService getSvc;
    private final FeatureDeleteService deleteSvc;

    public FeatureMcpTools(FeatureSearchService s, FeatureCreateService c, FeatureUpdateService u, FeatureGetByIdService g, FeatureDeleteService d) {
        this.searchSvc = s; this.createSvc = c; this.updateSvc = u; this.getSvc = g; this.deleteSvc = d;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("feature_search", "Search for features.",
                schema.object().prop("nameQuery", schema.string()).prop("projectName", schema.string()).prop("ownerLogin", schema.string())
                        .prop("startDate", schema.string()).prop("endDate", schema.string()).prop("sprintId", schema.integer())
                        .prop("take", schema.integer().withDefault(10)).build(),
                args -> search(new FeatureSearchService.SearchCriteria(args.path("nameQuery").asText(null), args.path("projectName").asText(null),
                        args.path("ownerLogin").asText(null), args.path("startDate").asText(null), args.path("endDate").asText(null),
                        args.path("take").asInt(10), args.has("sprintId") ? args.path("sprintId").asInt() : null)));

        server.registerTool("feature_create", "Create a new feature.",
                schema.object().prop("name", schema.string().required()).prop("projectId", schema.integer().required())
                        .prop("description", schema.string()).prop("effort", schema.number()).build(),
                args -> create(args.path("name").asText(), args.path("projectId").asInt(), args.path("description").asText(null), args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("feature_update", "Update an existing feature.",
                schema.object().prop("id", schema.integer().required()).prop("name", schema.string()).prop("description", schema.string())
                        .prop("stateName", schema.string()).prop("effort", schema.number()).build(),
                args -> update(args.path("id").asInt(), args.path("name").asText(null), args.path("description").asText(null), args.path("stateName").asText(null), args.has("effort") ? args.path("effort").asDouble() : null));

        server.registerTool("feature_get", "Get a feature by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> get(args.path("id").asInt()));

        server.registerTool("feature_delete", "Delete a feature by its numeric ID.",
                schema.object().prop("id", schema.integer().required()).build(), args -> delete(args.path("id").asInt()));
    }

    private String search(FeatureSearchService.SearchCriteria c) {
        var res = searchSvc.search(c);
        return res.isEmpty() ? "No features found." : String.join("\n", res.stream().map(this::format).toList());
    }

    private String create(String n, int p, String d, Double e) { return "Created: " + format(createSvc.create(n, p, d, e)); }
    private String update(int i, String n, String d, String s, Double e) { return "Updated: " + format(updateSvc.update(i, n, d, s, e)); }
    private String get(int i) { var f = getSvc.get(i); return format(f) + "\nDescription:\n" + (f.description() != null ? f.description() : "N/A"); }
    private String delete(int i) { deleteSvc.delete(i); return "Feature [%d] deleted successfully.".formatted(i); }

    private String format(FeatureDto f) {
        return "[%d] %s (Project: %s, State: %s, Author: %s, Points: %s, Created: %s, Done: %s, Sprint: %s)"
                .formatted(f.id(), f.name(), ns(f.projectName()), ns(f.state()), ns(f.ownerLogin()),
                        f.effort() != null ? f.effort().toString() : "N/A", ns(f.createdAt()), ns(f.endDate()),
                        f.sprintName() != null ? f.sprintId() + " " + f.sprintName() : "N/A");
    }

    private String ns(String v) { return v != null ? v : "N/A"; }
}

