package com.ibm.mcp.targetprocess.request.controller;

import com.ibm.mcp.targetprocess.request.dto.RequestDto;
import com.ibm.mcp.targetprocess.request.service.RequestCreateService;
import com.ibm.mcp.targetprocess.request.service.RequestSearchService;
import com.ibm.mcp.targetprocess.request.service.RequestUpdateService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestMcpTools {

    private final RequestSearchService requestSearchService;
    private final RequestCreateService requestCreateService;
    private final RequestUpdateService requestUpdateService;

    public RequestMcpTools(RequestSearchService requestSearchService,
                           RequestCreateService requestCreateService,
                           RequestUpdateService requestUpdateService) {
        this.requestSearchService = requestSearchService;
        this.requestCreateService = requestCreateService;
        this.requestUpdateService = requestUpdateService;
    }

    @Tool(description = """
            Search for requests in Targetprocess. \
            Supports filtering by request name, project name, owner login, \
            and creation date range (YYYY-MM-DD). Results are ordered by creation date descending.""")
    public String searchRequests(String nameQuery, String projectName,
                                 String ownerLogin, String startDate,
                                 String endDate, int take) {
        List<RequestDto> requests = requestSearchService.searchRequests(
                nameQuery, projectName, ownerLogin, startDate, endDate, take);

        if (requests.isEmpty()) {
            return "No requests found.";
        }

        return String.join("\n", requests.stream().map(this::format).toList());
    }

    @Tool(description = """
            Create a new request in Targetprocess. \
            Requires name and projectId (numeric ID of the project). \
            Description and effort (story points) are optional.""")
    public String createRequest(String name, int projectId, String description, Double effort) {
        RequestDto request = requestCreateService.createRequest(name, projectId, description, effort);
        return "Created: " + format(request);
    }

    @Tool(description = """
            Update an existing request in Targetprocess by its numeric ID. \
            All fields except id are optional — only provided (non-blank) fields are updated. \
            stateName accepts workflow state names such as 'Open', 'In Progress', 'Done'.""")
    public String updateRequest(int id, String name, String description, String stateName, Double effort) {
        RequestDto request = requestUpdateService.updateRequest(id, name, description, stateName, effort);
        return "Updated: " + format(request);
    }

    private String format(RequestDto r) {
        return "[%d] %s (Project: %s, State: %s, Owner: %s, Points: %s, Created: %s, Done: %s)"
            .formatted(
                r.id(), r.name(),
                nullSafe(r.projectName()), nullSafe(r.state()),
                nullSafe(r.ownerLogin()),
                r.effort() != null ? r.effort().toString() : "N/A",
                nullSafe(r.createdAt()), nullSafe(r.endDate())
            );
    }

    private String nullSafe(String v) { return v != null ? v : "N/A"; }
}
