package com.ibm.mcp.zdtp.release.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.release.entity.Release;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class ReleaseSearchService extends BaseService {
    private final ReleaseConverter converter;

    public ReleaseSearchService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, ReleaseConverter converter, ObjectMapper mapper) {
        super(properties, httpClient, mapper);
        this.converter = converter;
    }

    public record SearchCriteria(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take, Integer teamIterationId) {}

    public List<ReleaseDto> searchReleases(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take, Integer teamIterationId) {
        return search(new SearchCriteria(nameQuery, projectName, ownerLogin, startDate, endDate, take, teamIterationId));
    }

    public List<ReleaseDto> search(SearchCriteria criteria) {
        String whereClause = query()
                .add("Name", "contains", criteria.nameQuery())
                .add("Project.Name", "contains", criteria.projectName())
                .add("Owner.Login", "eq", criteria.ownerLogin())
                .add(criteria.startDate() != null && !criteria.startDate().isBlank() ? "StartDate gte '%s'".formatted(criteria.startDate()) : null)
                .add(criteria.endDate() != null && !criteria.endDate().isBlank() ? "StartDate lt '%s'".formatted(criteria.endDate()) : null)
                .add(criteria.teamIterationId() != null && criteria.teamIterationId() > 0 ? "TeamIteration.Id eq %d".formatted(criteria.teamIterationId()) : null)
                .build();

        Map<String, String> parameters = new TreeMap<>();
        if (!whereClause.isBlank()) {
            parameters.put("where", whereClause);
        }
        parameters.put("orderByDesc", "StartDate");
        parameters.put("take", String.valueOf(criteria.take()));

        return engine.list(QueryEngine.RELEASE, parameters, new TypeReference<>() {}, converter::toDto);
    }
}
