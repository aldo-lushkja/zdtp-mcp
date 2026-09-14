package com.ibm.mcp.zdtp.testrun.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.testrun.entity.TestRun;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestRunCreateServiceTest {

    @Mock TargetProcessHttpClient httpClient;

    @Test
    void create_postsTestRunBody() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        QueryEngine engine = new QueryEngine(new TargetProcessProperties("https://test.tpondemand.com", "token"), httpClient, mapper);
        TestRunCreateService service = new TestRunCreateService(engine);

        when(httpClient.post(anyString(), anyString())).thenReturn("{\"Id\":101,\"Name\":\"Regression Run #1\",\"Project\":{\"Id\":1,\"Name\":\"Proj\"},\"TestPlan\":{\"Id\":5,\"Name\":\"Plan\"}}");
        when(httpClient.parseSingle(anyString(), eq(com.ibm.mcp.zdtp.testrun.entity.TestRunDto.class))).thenCallRealMethod();

        TestRun testRun = service.create("Regression Run #1", 1, 5, "Run description");

        assertThat(testRun.id()).isEqualTo(101);
        assertThat(testRun.name()).isEqualTo("Regression Run #1");
        assertThat(testRun.projectId()).isEqualTo(1);
        assertThat(testRun.testPlanId()).isEqualTo(5);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).post(anyString(), bodyCaptor.capture());
        assertThat(bodyCaptor.getValue()).contains("Regression Run #1");
    }
}
