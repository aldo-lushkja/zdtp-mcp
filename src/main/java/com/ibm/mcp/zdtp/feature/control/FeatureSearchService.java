package com.ibm.mcp.zdtp.feature.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.type.TypeReference;

import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.feature.entity.Feature;
import com.ibm.mcp.zdtp.feature.entity.FeatureDto;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;

public class FeatureSearchService extends BaseService {
    private final FeatureConverter converter;

    public FeatureSearchService(QueryEngine engine, FeatureConverter converter) {
        super(engine);
        this.converter = converter;
    }

    public record SearchCriteria(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take, Integer skip, Integer sprintId) {
        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private String nameQuery; private String projectName; private String ownerLogin;
            private String startDate; private String endDate; private int take = 10; private Integer skip; private Integer sprintId;
            public Builder nameQuery(String val) { this.nameQuery = val; return this; }
            public Builder projectName(String val) { this.projectName = val; return this; }
            public Builder ownerLogin(String val) { this.ownerLogin = val; return this; }
            public Builder startDate(String val) { this.startDate = val; return this; }
            public Builder endDate(String val) { this.endDate = val; return this; }
            public Builder take(int val) { this.take = val; return this; }
            public Builder skip(Integer val) { this.skip = val; return this; }
            public Builder sprintId(Integer val) { this.sprintId = val; return this; }
            public SearchCriteria build() { return new SearchCriteria(nameQuery, projectName, ownerLogin, startDate, endDate, take, skip, sprintId); }
        }
    }

    public List<FeatureDto> searchFeatures(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take, Integer teamIterationId) {
        return search(new SearchCriteria(nameQuery, projectName, ownerLogin, startDate, endDate, take, null, teamIterationId));
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
        parameters.put("orderByDesc", "CreateDate");
        parameters.put("take", String.valueOf(criteria.take()));
        if (criteria.skip() != null && criteria.skip() > 0) {
            parameters.put("skip", String.valueOf(criteria.skip()));
        }

        return engine.list(QueryEngine.FEATURE, parameters, new TypeReference<>() {}, converter::toDto);
    }
}

