package com.ibm.mcp.zdtp.testplan.control;

import java.util.Map;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.BaseService;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;

public class TestPlanGetByIdService extends BaseService {
    private static final String INCLUDE = "[Id,Name,Description,Project[Id,Name],EntityState[Id,Name],CreateDate,Owner[Id,Login]]";
    private final TestPlanConverter converter;

    public TestPlanGetByIdService(TargetProcessProperties properties, TargetProcessHttpClient httpClient, TestPlanConverter converter) {
        super(properties, httpClient);
        this.converter = converter;
    }

    public TestPlanDto getById(int id) {
        return get(id);
    }

    public TestPlanDto get(int id) {
        Map<String, String> parameters = Map.of("include", INCLUDE);
        return fetchSingle("TestPlans/" + id, parameters, TestPlan.class, converter::toDto);
    }
}
