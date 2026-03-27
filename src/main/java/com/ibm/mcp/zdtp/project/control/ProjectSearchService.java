package com.ibm.mcp.zdtp.project.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.type.TypeReference;

import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.project.entity.ProjectData;
import com.ibm.mcp.zdtp.project.entity.ProjectDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;

public class ProjectSearchService extends BaseService {
    private final ProjectConverter converter;

    public ProjectSearchService(QueryEngine engine, ProjectConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public record SearchCriteria(String nameQuery, String startDate, String endDate, int take) {}

    public List<ProjectDto> searchProjects(String nameQuery, String startDate, String endDate, int take) {
        return search(new SearchCriteria(nameQuery, startDate, endDate, take));
    }

    public List<ProjectDto> search(SearchCriteria criteria) {
        String whereClause = query()
                .add("Name", "contains", criteria.nameQuery())
                .add(criteria.startDate() != null && !criteria.startDate().isBlank() ? "CreateDate gte '%s'".formatted(criteria.startDate()) : null)
                .add(criteria.endDate() != null && !criteria.endDate().isBlank() ? "CreateDate lt '%s'".formatted(criteria.endDate()) : null)
                .build();

        Map<String, String> parameters = new TreeMap<>();
        if (!whereClause.isBlank()) {
            parameters.put("where", whereClause);
        }
        parameters.put("orderByDesc", "CreateDate");
        parameters.put("take", String.valueOf(criteria.take()));

        return engine.list(QueryEngine.PROJECT, parameters, new TypeReference<>() {}, converter::toDto);
    }
}

