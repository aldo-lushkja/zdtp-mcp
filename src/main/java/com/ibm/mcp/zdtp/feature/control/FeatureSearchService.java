package com.ibm.mcp.zdtp.feature.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class FeatureSearchService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login],TeamIteration[Id,Name]]";
    private final FeatureConverter converter;

    public FeatureSearchService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, FeatureConverter converter) {
        super(properties, httpClient);
        this.converter = converter;
    }

    public record SearchCriteria(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take, Integer sprintId) {}

    public List<FeatureDto> searchFeatures(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take, Integer teamIterationId) {
        return search(new SearchCriteria(nameQuery, projectName, ownerLogin, startDate, endDate, take, teamIterationId));
    }

    public List<FeatureDto> search(SearchCriteria criteria) {
        String whereClause = query()
                .add("Name", "contains", criteria.nameQuery())
                .add("Project.Name", "contains", criteria.projectName())
                .add("Owner.Login", "eq", criteria.ownerLogin())
                .add(criteria.startDate() != null && !criteria.startDate().isBlank() ? "CreateDate gte '%s'".formatted(criteria.startDate()) : null)
                .add(criteria.endDate() != null && !criteria.endDate().isBlank() ? "CreateDate lt '%s'".formatted(criteria.endDate()) : null)
                .add(criteria.sprintId() != null && criteria.sprintId() > 0 ? "TeamIteration.Id eq %d".formatted(criteria.sprintId()) : null)
                .build();

        Map<String, String> parameters = new TreeMap<>();
        if (!whereClause.isBlank()) {
            parameters.put("where", whereClause);
        }
        parameters.put("include", INCLUDE);
        parameters.put("orderByDesc", "CreateDate");
        parameters.put("take", String.valueOf(criteria.take()));

        return fetchList("Features", parameters, new TypeReference<>() {}, converter::toDto);
    }
}
