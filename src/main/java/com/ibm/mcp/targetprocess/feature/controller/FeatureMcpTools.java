package com.ibm.mcp.targetprocess.feature.controller;

import com.ibm.mcp.targetprocess.feature.dto.FeatureDto;
import com.ibm.mcp.targetprocess.feature.service.FeatureCreateService;
import com.ibm.mcp.targetprocess.feature.service.FeatureGetByIdService;
import com.ibm.mcp.targetprocess.feature.service.FeatureSearchService;
import com.ibm.mcp.targetprocess.feature.service.FeatureUpdateService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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

    @Tool(description = """
            Search for features in Targetprocess. \
            Supports filtering by feature name, project name, owner login, \
            creation date range (YYYY-MM-DD), and sprintId (numeric team iteration ID). \
            Results are ordered by creation date descending.""")
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

    @Tool(description = """
            Create a new feature in Targetprocess. \
            Requires name and projectId (numeric ID of the project). \
            Description and effort (story points) are optional. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String createFeature(String name, int projectId, String description, Double effort) {
        FeatureDto feature = featureCreateService.createFeature(name, projectId, description, effort);
        return "Created: " + format(feature);
    }

    @Tool(description = """
            Update an existing feature in Targetprocess by its numeric ID. \
            All fields except id are optional — only provided (non-blank) fields are updated. \
            stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String updateFeature(int id, String name, String description, String stateName, Double effort) {
        FeatureDto feature = featureUpdateService.updateFeature(id, name, description, stateName, effort);
        return "Updated: " + format(feature);
    }

    @Tool(description = "Get a feature by its numeric ID. Returns full details including description.")
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
