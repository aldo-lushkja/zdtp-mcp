package com.ibm.mcp.targetprocess.feature.controller;

import com.ibm.mcp.targetprocess.feature.dto.FeatureDto;
import com.ibm.mcp.targetprocess.feature.service.FeatureSearchService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeatureMcpTools {

    private final FeatureSearchService featureSearchService;

    public FeatureMcpTools(FeatureSearchService featureSearchService) {
        this.featureSearchService = featureSearchService;
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
