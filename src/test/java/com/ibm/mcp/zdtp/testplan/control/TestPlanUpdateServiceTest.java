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
class TestPlanUpdateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String PLAN_RESPONSE = """
            {"Id":1,"Name":"Updated Plan","Project":{"Id":42,"Name":"P1"},
            "EntityState":{"Id":2,"Name":"Active"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "Owner":{"Id":5,"Login":"owner@test.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    TestPlanUpdateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new TestPlanUpdateService(engine, new TestPlanConverter());
    }

    @Test
    void update_urlContainsTestPlanId() {
        givenApiReturns(PLAN_RESPONSE);
        service.updateTestPlan(1, "New Name", null, null);
        assertThat(captureUrl()).contains("/api/v1/TestPlans/1");
    }

    @Test
    void update_urlContainsFormatJson() {
        givenApiReturns(PLAN_RESPONSE);
        service.updateTestPlan(1, "New Name", null, null);
        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void update_urlContainsInclude() {
        givenApiReturns(PLAN_RESPONSE);
        service.updateTestPlan(1, "New Name", null, null);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("Owner");
    }

    @Test
    void update_bodyContainsNameWhenProvided() {
        givenApiReturns(PLAN_RESPONSE);
        service.updateTestPlan(1, "New Name", null, null);
        assertThat(captureBody()).contains("\"Name\":\"New Name\"");
    }

    @Test
    void update_bodyOmitsNameWhenBlank() {
        givenApiReturns(PLAN_RESPONSE);
        service.updateTestPlan(1, "", "Desc", null);
        assertThat(captureBody()).doesNotContain("\"Name\":");
    }

    @Test
    void update_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(PLAN_RESPONSE);
        service.updateTestPlan(1, null, "New Description", null);
        assertThat(captureBody()).contains("\"Description\":\"New Description\"");
    }

    @Test
    void update_bodyContainsEntityStateWhenProvided() {
        givenApiReturns(PLAN_RESPONSE);
        service.updateTestPlan(1, null, null, "Active");
        assertThat(captureBody()).contains("\"EntityState\":{\"Name\":\"Active\"}");
    }

    @Test
    void update_bodyOmitsEntityStateWhenBlank() {
        givenApiReturns(PLAN_RESPONSE);
        service.updateTestPlan(1, null, null, "");
        assertThat(captureBody()).doesNotContain("EntityState");
    }

    @Test
    void update_emptyBody_whenAllFieldsNull() {
        givenApiReturns(PLAN_RESPONSE);
        service.updateTestPlan(1, null, null, null);
        assertThat(captureBody()).isEqualTo("{}");
    }

    @Test
    void update_mapsResponseCorrectly() {
        givenApiReturns(PLAN_RESPONSE);
        TestPlanDto result = service.updateTestPlan(1, "Updated Plan", null, null);
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("Updated Plan");
    }

    @Test
    void update_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any())).thenThrow(new TargetProcessApiException(400, "Bad Request"));
        assertThatThrownBy(() -> service.updateTestPlan(1, "Name", null, null))
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






