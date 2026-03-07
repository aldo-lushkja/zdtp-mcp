package com.ibm.mcp.zdtp.feature.boundary;

import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.feature.control.FeatureCreateService;
import com.ibm.mcp.zdtp.feature.control.FeatureGetByIdService;
import com.ibm.mcp.zdtp.feature.control.FeatureSearchService;
import com.ibm.mcp.zdtp.feature.control.FeatureUpdateService;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;

import java.util.List;

public class FeatureMcpTools {

    private final FeatureSearchService featureSearchService;
    private final FeatureCreateService featureCreateService;
    private final FeatureUpdateService featureUpdateService;
    private final FeatureGetByIdService featureGetByIdService;

    public FeatureMcpTools(FeatureSearchService featureSearchService,
                           FeatureCreateService featureCreateService,
                           FeatureUpdateService featureUpdateService,
                           FeatureGetByIdService featureGetByIdService) {
        this.featureSearchService = featureSearchService;
        this.featureCreateService = featureCreateService;
        this.featureUpdateService = featureUpdateService;
        this.featureGetByIdService = featureGetByIdService;
    }

    public void register(McpServer server, SchemaBuilder schema) {
        server.registerTool("feature_search", 
            "Search for features in Targetprocess. Supports filtering by feature name, project name, owner login, creation date range (YYYY-MM-DD), and sprintId (numeric team iteration ID). Results are ordered by creation date descending.",
            schema.object()
                .prop("nameQuery", schema.string())
                .prop("projectName", schema.string())
                .prop("ownerLogin", schema.string())
                .prop("startDate", schema.string())
                .prop("endDate", schema.string())
                .prop("take", schema.integer().withDefault(10))
                .prop("sprintId", schema.integer())
                .build(),
            args -> searchFeatures(
                args.path("nameQuery").asText(null),
                args.path("projectName").asText(null),
                args.path("ownerLogin").asText(null),
                args.path("startDate").asText(null),
                args.path("endDate").asText(null),
                args.path("take").asInt(10),
                args.has("sprintId") ? args.path("sprintId").asInt() : null
            )
        );

        server.registerTool("feature_create",
            "Create a new feature in Targetprocess. Requires name and projectId (numeric ID of the project). Description and effort (story points) are optional. IMPORTANT — description formatting rules: (1) Always use HTML, never plain markdown. (2) To embed a diagram, encode the Mermaid definition in base64 and use: <img src=\"https://mermaid.ink/img/<base64>\" alt=\"diagram description\" />",
            schema.object()
                .prop("name", schema.string().required())
                .prop("projectId", schema.integer().required())
                .prop("description", schema.string())
                .prop("effort", schema.number())
                .build(),
            args -> createFeature(
                args.path("name").asText(),
                args.path("projectId").asInt(),
                args.path("description").asText(null),
                args.has("effort") ? args.path("effort").asDouble() : null
            )
        );

        server.registerTool("feature_update",
            "Update an existing feature in Targetprocess by its numeric ID. All fields except id are optional — only provided (non-blank) fields are updated. stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'.",
            schema.object()
                .prop("id", schema.integer().required())
                .prop("name", schema.string())
                .prop("description", schema.string())
                .prop("stateName", schema.string())
                .prop("effort", schema.number())
                .build(),
            args -> updateFeature(
                args.path("id").asInt(),
                args.path("name").asText(null),
                args.path("description").asText(null),
                args.path("stateName").asText(null),
                args.has("effort") ? args.path("effort").asDouble() : null
            )
        );

        server.registerTool("feature_get",
            "Get a feature by its numeric ID. Returns full details including description.",
            schema.object()
                .prop("id", schema.integer().required())
                .build(),
            args -> getFeatureById(args.path("id").asInt())
        );
    }

    public String searchFeatures(String nameQuery, String projectName,
                                 String ownerLogin, String startDate,
                                 String endDate, int take, Integer sprintId) {
        List<FeatureDto> features = featureSearchService.searchFeatures(
                nameQuery, projectName, ownerLogin, startDate, endDate, take, sprintId);

        if (features.isEmpty()) {
            return "No features found.";
        }

        return String.join("\n", features.stream().map(this::format).toList());
    }

    public String createFeature(String name, int projectId, String description, Double effort) {
        FeatureDto feature = featureCreateService.createFeature(name, projectId, description, effort);
        return "Created: " + format(feature);
    }

    public String updateFeature(int id, String name, String description, String stateName, Double effort) {
        FeatureDto feature = featureUpdateService.updateFeature(id, name, description, stateName, effort);
        return "Updated: " + format(feature);
    }

    public String getFeatureById(int id) {
        FeatureDto feature = featureGetByIdService.getById(id);
        return format(feature) + "\nDescription:\n" + nullSafe(feature.description());
    }

    private String format(FeatureDto f) {
        return "[%d] %s (Project: %s, State: %s, Owner: %s, Points: %s, Created: %s, Done: %s, Sprint: %s)"
            .formatted(
                f.id(), f.name(),
                nullSafe(f.projectName()), nullSafe(f.state()),
                nullSafe(f.ownerLogin()),
                f.effort() != null ? f.effort().toString() : "N/A",
                nullSafe(f.createdAt()), nullSafe(f.endDate()),
                f.sprintName() != null ? f.sprintId() + " " + f.sprintName() : "N/A"
            );
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}
