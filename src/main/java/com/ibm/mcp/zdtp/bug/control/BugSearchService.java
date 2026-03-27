package com.ibm.mcp.zdtp.bug.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.bug.entity.BugDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

public class BugSearchService extends BaseService {
    private final BugConverter converter;

    public BugSearchService(QueryEngine engine, BugConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public record SearchCriteria(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take, Integer userStoryId, Integer featureId) {}

    public List<BugDto> search(SearchCriteria criteria) {
        String whereClause = query()
                .add("Name", "contains", criteria.nameQuery())
                .add("Project.Name", "contains", criteria.projectName())
                .add("Owner.Login", "eq", criteria.ownerLogin())
                .add(criteria.startDate() != null && !criteria.startDate().isBlank() ? "CreateDate gte '%s'".formatted(criteria.startDate()) : null)
                .add(criteria.endDate() != null && !criteria.endDate().isBlank() ? "CreateDate lt '%s'".formatted(criteria.endDate()) : null)
                .add(criteria.userStoryId() != null && criteria.userStoryId() > 0 ? "UserStory.Id eq %d".formatted(criteria.userStoryId()) : null)
                .add(criteria.featureId() != null && criteria.featureId() > 0 ? "Feature.Id eq %d".formatted(criteria.featureId()) : null)
                .build();

        Map<String, String> parameters = new TreeMap<>();
        if (!whereClause.isBlank()) {
            parameters.put("where", whereClause);
        }
        parameters.put("orderByDesc", "CreateDate");
        parameters.put("take", String.valueOf(criteria.take()));

        return engine.list(QueryEngine.BUG, parameters, new TypeReference<>() {}, converter::toDto);
    }
}
