package com.ibm.mcp.targetprocess.release.controller;

import com.ibm.mcp.targetprocess.release.dto.ReleaseDto;
import com.ibm.mcp.targetprocess.release.service.ReleaseCreateService;
import com.ibm.mcp.targetprocess.release.service.ReleaseGetByIdService;
import com.ibm.mcp.targetprocess.release.service.ReleaseSearchService;
import com.ibm.mcp.targetprocess.release.service.ReleaseUpdateService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReleaseMcpTools {

    private final ReleaseSearchService releaseSearchService;
    private final ReleaseCreateService releaseCreateService;
    private final ReleaseUpdateService releaseUpdateService;
    private final ReleaseGetByIdService releaseGetByIdService;

    public ReleaseMcpTools(ReleaseSearchService releaseSearchService,
                           ReleaseCreateService releaseCreateService,
                           ReleaseUpdateService releaseUpdateService,
                           ReleaseGetByIdService releaseGetByIdService) {
        this.releaseSearchService = releaseSearchService;
        this.releaseCreateService = releaseCreateService;
        this.releaseUpdateService = releaseUpdateService;
        this.releaseGetByIdService = releaseGetByIdService;
    }

    @Tool(description = """
            Search for releases in Targetprocess. \
            Supports filtering by release name, project name, owner login, \
            and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.""")
    public String searchReleases(String nameQuery, String projectName,
                                 String ownerLogin, String startDate,
                                 String endDate, int take) {
        List<ReleaseDto> releases = releaseSearchService.searchReleases(
                nameQuery, projectName, ownerLogin, startDate, endDate, take);

        if (releases.isEmpty()) {
            return "No releases found.";
        }

        return String.join("\n", releases.stream().map(this::format).toList());
    }

    @Tool(description = """
            Create a new release in Targetprocess. \
            Requires name and projectId (numeric ID of the project). \
            Description and effort (story points) are optional. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String createRelease(String name, int projectId, String description, Double effort) {
        ReleaseDto release = releaseCreateService.createRelease(name, projectId, description, effort);
        return "Created: " + format(release);
    }

    @Tool(description = """
            Update an existing release in Targetprocess by its numeric ID. \
            All fields except id are optional — only provided (non-blank) fields are updated. \
            stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'. \
            IMPORTANT — description formatting rules: \
            (1) Always use HTML, never plain markdown (e.g. <h2>, <p>, <ul>, <li>, <strong>). \
            (2) To embed a diagram, encode the Mermaid definition in base64 and use: \
            <img src="https://mermaid.ink/img/<base64>" alt="diagram description" />""")
    public String updateRelease(int id, String name, String description, String stateName, Double effort) {
        ReleaseDto release = releaseUpdateService.updateRelease(id, name, description, stateName, effort);
        return "Updated: " + format(release);
    }

    @Tool(description = "Get a release by its numeric ID. Returns full details including description.")
    public String getReleaseById(int id) {
        ReleaseDto release = releaseGetByIdService.getById(id);
        return format(release) + "\nDescription:\n" + nullSafe(release.description());
    }

    private String format(ReleaseDto r) {
        return "[%d] %s (Project: %s, State: %s, Owner: %s, Points: %s, Created: %s, Start: %s, End: %s)"
            .formatted(
                r.id(), r.name(),
                nullSafe(r.projectName()), nullSafe(r.state()),
                nullSafe(r.ownerLogin()),
                r.effort() != null ? r.effort().toString() : "N/A",
                nullSafe(r.createdAt()), nullSafe(r.startDate()), nullSafe(r.endDate())
            );
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}
