package com.ibm.mcp.zdtp.project.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.project.entity.ProjectData;
import com.ibm.mcp.zdtp.project.entity.ProjectDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class ProjectSearchService extends BaseService {
    private final ProjectConverter converter;

    public ProjectSearchService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, ProjectConverter converter, ObjectMapper objectMapper) {
        super(properties, httpClient, objectMapper);
        this.converter = converter;
    }

    public record SearchCriteria(String nameQuery, String startDate, String endDate, int take) {}

    public List<ProjectDto> searchProjects(String nameQuery, String startDate, String endDate, int take) {
        return search(new SearchCriteria(nameQuery, startDate, endDate, take));
    }

    public List<ProjectDto> search(SearchCriteria criteria) {
        String where = query().add("Name", "contains", criteria.nameQuery())
                .add(criteria.startDate() != null && !criteria.startDate().isBlank() ? "CreateDate gte '%s'".formatted(criteria.startDate()) : null)
                .add(criteria.endDate() != null && !criteria.endDate().isBlank() ? "CreateDate lt '%s'".formatted(criteria.endDate()) : null).build();
        Map<String, String> p = new TreeMap<>(); if (!where.isBlank()) p.put("where", where);
        p.put("orderByDesc", "CreateDate"); p.put("take", String.valueOf(criteria.take()));
        return engine.list(QueryEngine.PROJECT, p, new TypeReference<>() {}, converter::toDto);
    }
}
