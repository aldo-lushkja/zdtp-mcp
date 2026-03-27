package com.ibm.mcp.zdtp.testplan.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.testplan.entity.TestPlanDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestPlanSearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String PLANS_RESPONSE = """
            {"Items":[{"Id":1,"Name":"Plan 1"},{"Id":2,"Name":"Plan 2"}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    TestPlanSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new TestPlanSearchService(engine, new TestPlanConverter());
    }

    @Test
    void search_returnsMappedPlans() {
        when(httpClient.fetch(any())).thenReturn(PLANS_RESPONSE);
        when(httpClient.parse(any(), any())).thenCallRealMethod();

        List<TestPlanDto> result = service.searchTestPlans(null, null, null, null, null, 10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Plan 1");
    }

    @Test
    void search_buildsCorrectUrl() {
        when(httpClient.fetch(any())).thenReturn(PLANS_RESPONSE);
        when(httpClient.parse(any(), any())).thenCallRealMethod();

        service.searchTestPlans("test", "P1", "owner", null, null, 5);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).fetch(urlCaptor.capture());
        String url = urlCaptor.getValue();

        assertThat(url).contains("/api/v1/TestPlans");
        assertThat(URLDecoder.decode(url, StandardCharsets.UTF_8)).contains("where=Name contains 'test' and Project.Name contains 'P1' and Owner.Login eq 'owner'");
        assertThat(url).contains("take=5");
    }
}






