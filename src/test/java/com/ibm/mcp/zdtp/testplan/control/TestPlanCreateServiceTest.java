package com.ibm.mcp.zdtp.testplan.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.exception.TargetProcessApiException;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;
import com.ibm.mcp.zdtp.testplan.entity.TestPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestPlanCreateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String PLAN_RESPONSE = """
            {"Id":1,"Name":"New Plan","Project":{"Id":42,"Name":"P1"},
            "EntityState":{"Id":2,"Name":"Active"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "Owner":{"Id":5,"Login":"owner@test.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    TestPlanCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new TestPlanCreateService(engine, new TestPlanConverter());
    }

    @Test
    void create_urlPointsToTestPlansEndpoint() {
        givenApiReturns(PLAN_RESPONSE);
        service.createTestPlan("New Plan", 42, null);
        assertThat(captureUrl()).contains("/api/v1/TestPlans");
    }

    @Test
    void create_urlContainsFormatJson() {
        givenApiReturns(PLAN_RESPONSE);
        service.createTestPlan("New Plan", 42, null);
        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void create_urlContainsInclude() {
        givenApiReturns(PLAN_RESPONSE);
        service.createTestPlan("New Plan", 42, null);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("Owner");
    }

    @Test
    void create_bodyContainsNameAndProjectId() {
        givenApiReturns(PLAN_RESPONSE);
        service.createTestPlan("New Plan", 42, null);
        String body = captureBody();
        assertThat(body).contains("\"Name\":\"New Plan\"");
        assertThat(body).contains("\"Id\":42");
    }

    @Test
    void create_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(PLAN_RESPONSE);
        service.createTestPlan("New Plan", 42, "Plan description");
        assertThat(captureBody()).contains("\"Description\":\"Plan description\"");
    }

    @Test
    void create_bodyOmitsDescriptionWhenBlank() {
        givenApiReturns(PLAN_RESPONSE);
        service.createTestPlan("New Plan", 42, "");
        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_bodyOmitsDescriptionWhenNull() {
        givenApiReturns(PLAN_RESPONSE);
        service.createTestPlan("New Plan", 42, null);
        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_mapsResponseCorrectly() {
        givenApiReturns(PLAN_RESPONSE);
        TestPlanDto result = service.createTestPlan("New Plan", 42, null);
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("New Plan");
        assertThat(result.ownerLogin()).isEqualTo("owner@test.com");
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void create_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any())).thenThrow(new TargetProcessApiException(400, "Bad Request"));
        assertThatThrownBy(() -> service.createTestPlan("Bad", 0, null))
                .isInstanceOf(TargetProcessApiException.class);
    }

    private void givenApiReturns(String body) {
        when(httpClient.post(any(), any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(TestPlan.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body, TestPlan.class));
    }

    private String captureUrl() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).post(captor.capture(), any());
        return captor.getValue();
    }

    private String captureBody() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).post(any(), captor.capture());
        return captor.getValue();
    }
}






