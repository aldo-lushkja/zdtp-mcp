package com.ibm.mcp.zdtp.epic.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.epic.entity.Epic;
import com.ibm.mcp.zdtp.epic.entity.EpicDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;

public class EpicSearchService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,EndDate,Effort,Owner[Id,Login]]";
    private final EpicConverter converter;

    public EpicSearchService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, EpicConverter converter) {
        super(properties, httpClient);
        this.converter = converter;
    }

    public record SearchCriteria(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take) {}

    public List<EpicDto> searchEpics(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take) {
        return search(new SearchCriteria(nameQuery, projectName, ownerLogin, startDate, endDate, take));
    }

    public List<EpicDto> search(SearchCriteria criteria) {
        String whereClause = query()
                .add("Name", "contains", criteria.nameQuery())
                .add("Project.Name", "contains", criteria.projectName())
                .add("Owner.Login", "eq", criteria.ownerLogin())
                .add(criteria.startDate() != null && !criteria.startDate().isBlank() ? "CreateDate gte '%s'".formatted(criteria.startDate()) : null)
                .add(criteria.endDate() != null && !criteria.endDate().isBlank() ? "CreateDate lt '%s'".formatted(criteria.endDate()) : null)
                .build();

        Map<String, String> parameters = new TreeMap<>();
        if (!whereClause.isBlank()) {
            parameters.put("where", whereClause);
        }
        parameters.put("include", INCLUDE);
        parameters.put("orderByDesc", "CreateDate");
        parameters.put("take", String.valueOf(criteria.take()));

        return fetchList("Epics", parameters, new TypeReference<>() {}, converter::toDto);
    }
}
