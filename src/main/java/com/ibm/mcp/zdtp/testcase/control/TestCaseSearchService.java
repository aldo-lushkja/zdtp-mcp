package com.ibm.mcp.zdtp.testcase.control;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.QueryEngine;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testcase.entity.TestCase;
import com.ibm.mcp.zdtp.testcase.entity.TestCaseDto;

public class TestCaseSearchService extends BaseService {
    private final TestCaseConverter converter;

    public TestCaseSearchService(TargetProcessProperties props, TargetProcessHttpClient http, TestCaseConverter conv, ObjectMapper mapper) {
        super(props, http, mapper); this.converter = conv;
    }

    public record SearchCriteria(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take) {}

    public List<TestCaseDto> searchTestCases(String nameQuery, String projectName, String ownerLogin, String startDate, String endDate, int take) {
        return search(new SearchCriteria(nameQuery, projectName, ownerLogin, startDate, endDate, take));
    }

    public List<TestCaseDto> search(SearchCriteria criteria) {
        String where = query().add("Name", "contains", criteria.nameQuery()).add("Project.Name", "contains", criteria.projectName()).add("Owner.Login", "eq", criteria.ownerLogin())
                .add(criteria.startDate() != null && !criteria.startDate().isBlank() ? "CreateDate gte '%s'".formatted(criteria.startDate()) : null)
                .add(criteria.endDate() != null && !criteria.endDate().isBlank() ? "CreateDate lt '%s'".formatted(criteria.endDate()) : null).build();
        Map<String, String> p = new TreeMap<>(); if (!where.isBlank()) p.put("where", where);
        p.put("orderByDesc", "CreateDate"); p.put("take", String.valueOf(criteria.take()));
        return engine.list(QueryEngine.TEST_CASE, p, new TypeReference<>() {}, converter::toDto);
    }
}
