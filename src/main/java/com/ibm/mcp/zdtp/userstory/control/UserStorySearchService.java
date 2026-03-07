package com.ibm.mcp.zdtp.userstory.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;

public class UserStorySearchService extends BaseService {
    private final UserStoryConverter converter;

    public UserStorySearchService(QueryEngine engine, UserStoryConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public record SearchCriteria(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take, Integer releaseId, Integer sprintId) {}

    public List<UserStoryDto> searchUserStories(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take, Integer releaseId, Integer sprintId) {
        return search(new SearchCriteria(nameQuery, projectName, ownerLogin, startDate, endDate, take, releaseId, sprintId));
    }

    public List<UserStoryDto> search(SearchCriteria criteria) {
        String whereClause = query()
                .add("Name", "contains", criteria.nameQuery())
                .add("Project.Name", "contains", criteria.projectName())
                .add("Owner.Login", "eq", criteria.ownerLogin())
                .add(criteria.startDate() != null && !criteria.startDate().isBlank() ? "CreateDate gte '%s'".formatted(criteria.startDate()) : null)
                .add(criteria.endDate() != null && !criteria.endDate().isBlank() ? "CreateDate lt '%s'".formatted(criteria.endDate()) : null)
                .add(criteria.releaseId() != null && criteria.releaseId() > 0 ? "Release.Id eq %d".formatted(criteria.releaseId()) : null)
                .add(criteria.sprintId() != null && criteria.sprintId() > 0 ? "TeamIteration.Id eq %d".formatted(criteria.sprintId()) : null)
                .build();

        Map<String, String> parameters = new TreeMap<>();
        if (!whereClause.isBlank()) {
            parameters.put("where", whereClause);
        }
        parameters.put("orderByDesc", "CreateDate");
        parameters.put("take", String.valueOf(criteria.take()));

        return engine.list(QueryEngine.USER_STORY, parameters, new TypeReference<>() {}, converter::toDto);
    }
}
