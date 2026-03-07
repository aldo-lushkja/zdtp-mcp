package com.ibm.mcp.zdtp.testcase.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testcase.entity.TestStep;
import com.ibm.mcp.zdtp.testcase.entity.TestStepDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestStepCreateServiceTest {

    private static final String BASE_URL = "http://test.com";
    private static final String TOKEN = "token";
    private static final String STEP_RESPONSE = """
                {
                    "Id": 101,
                    "Description": "Step 1",
                    "Result": "Success",
                    "RunOrder": 1,
                    "TestCase": { "Id": 456, "Name": "TC" }
                }
                """;

    @Mock
    private TargetProcessHttpClient httpClient;

    private TestStepCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties properties = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(properties, httpClient, new ObjectMapper());
        service = new TestStepCreateService(engine, new TestStepConverter());
    }

    @Test
    void create_callsApiAndReturnsDto() {
        givenApiReturns(STEP_RESPONSE);

        TestStepDto result = service.create(456, "Step 1", "Success", 1);

        assertThat(result.id()).isEqualTo(101);
        assertThat(result.description()).isEqualTo("Step 1");
        assertThat(result.expectedResult()).isEqualTo("Success");
        assertThat(result.runOrder()).isEqualTo(1);
        assertThat(result.testCaseId()).isEqualTo(456);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).post(contains("TestSteps"), bodyCaptor.capture());
        
        String sentBody = bodyCaptor.getValue();
        assertThat(sentBody).contains("\"TestCase\":{\"Id\":456}");
        assertThat(sentBody).contains("\"Description\":\"Step 1\"");
        assertThat(sentBody).contains("\"Result\":\"Success\"");
        assertThat(sentBody).contains("\"RunOrder\":1");
    }

    private void givenApiReturns(String body) {
        when(httpClient.post(anyString(), anyString())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(TestStep.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body, TestStep.class));
    }
}






