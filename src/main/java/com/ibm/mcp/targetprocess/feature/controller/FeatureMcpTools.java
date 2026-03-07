package com.ibm.mcp.targetprocess.feature.controller;

import com.ibm.mcp.targetprocess.feature.dto.FeatureDto;
import com.ibm.mcp.targetprocess.feature.service.FeatureCreateService;
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

    public FeatureMcpTools(FeatureSearchService featureSearchService,
                           FeatureCreateService featureCreateService,
                           FeatureUpdateService featureUpdateService) {
        this.featureSearchService = featureSearchService;
        this.featureCreateService = featureCreateService;
        this.featureUpdateService = featureUpdateService;
    }

    @Tool(description = """
            Search for features in Targetprocess. \
            Supports filtering by feature name, project name, owner login, \
            and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.""")
    public String searchFeatures(String nameQuery, String projectName,
                                 String ownerLogin, String startDate,
                                 String endDate, int take) {
        List<FeatureDto> features = featureSearchService.searchFeatures(
                nameQuery, projectName, ownerLogin, startDate, endDate, take);

        if (features.isEmpty()) {
            return "No features found.";
        }

        return String.join("\n", features.stream().map(this::format).toList());
    }

    @Tool(description = """
            Create a new feature in Targetprocess. \
            Requires name and projectId (numeric ID of the project). \
            Description and effort (story points) are optional.""")
    public String createFeature(String name, int projectId, String description, Double effort) {
        FeatureDto feature = featureCreateService.createFeature(name, projectId, description, effort);
        return "Created: " + format(feature);
    }

    @Tool(description = """
            Update an existing feature in Targetprocess by its numeric ID. \
            All fields except id are optional — only provided (non-blank) fields are updated. \
            stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'.""")
    public String updateFeature(int id, String name, String description, String stateName, Double effort) {
        FeatureDto feature = featureUpdateService.updateFeature(id, name, description, stateName, effort);
        return "Updated: " + format(feature);
    }

    private String format(FeatureDto f) {
        return "[%d] %s (Project: %s, State: %s, Owner: %s, Points: %s, Created: %s, Done: %s)"
            .formatted(
                f.id(), f.name(),
                nullSafe(f.projectName()), nullSafe(f.state()),
                nullSafe(f.ownerLogin()),
                f.effort() != null ? f.effort().toString() : "N/A",
                nullSafe(f.createdAt()), nullSafe(f.endDate())
            );
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}
