package com.ibm.mcp.zdtp.release.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.exception.TargetProcessApiException;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.release.entity.Release;
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
class ReleaseCreateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String RELEASE_RESPONSE = """
            {"Id":123,"Name":"New Release","Project":{"Id":1,"Name":"P1"},
            "EntityState":{"Id":2,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "StartDate":"\\/Date(1736899200000+0000)\\/",
            "EndDate":null,"Effort":5.0,
            "Owner":{"Id":5,"Login":"owner@test.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    ReleaseCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new ReleaseCreateService(engine, new ReleaseConverter());
    }

    @Test
    void create_urlPointsToReleasesEndpoint() {
        givenApiReturns(RELEASE_RESPONSE);
        service.createRelease("New Release", 42, null, null);
        assertThat(captureUrl()).contains("/api/v1/Releases");
    }

    @Test
    void create_urlContainsFormatJson() {
        givenApiReturns(RELEASE_RESPONSE);
        service.createRelease("New Release", 42, null, null);
        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void create_urlContainsIncludeWithStartDate() {
        givenApiReturns(RELEASE_RESPONSE);
        service.createRelease("New Release", 42, null, null);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("StartDate");
    }

    @Test
    void create_bodyContainsNameAndProjectId() {
        givenApiReturns(RELEASE_RESPONSE);
        service.createRelease("New Release", 42, null, null);
        String body = captureBody();
        assertThat(body).contains("\"Name\":\"New Release\"");
        assertThat(body).contains("\"Id\":42");
    }

    @Test
    void create_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(RELEASE_RESPONSE);
        service.createRelease("New Release", 42, "Release description", null);
        assertThat(captureBody()).contains("\"Description\":\"Release description\"");
    }

    @Test
    void create_bodyOmitsDescriptionWhenBlank() {
        givenApiReturns(RELEASE_RESPONSE);
        service.createRelease("New Release", 42, "", null);
        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_bodyContainsEffortWhenProvided() {
        givenApiReturns(RELEASE_RESPONSE);
        service.createRelease("New Release", 42, null, 13.0);
        assertThat(captureBody()).contains("\"Effort\":13.0");
    }

    @Test
    void create_mapsResponseCorrectly() {
        givenApiReturns(RELEASE_RESPONSE);
        ReleaseDto result = service.createRelease("New Release", 42, null, null);
        assertThat(result.id()).isEqualTo(123);
        assertThat(result.name()).isEqualTo("New Release");
        assertThat(result.startDate()).isEqualTo("2025-01-15");
    }

    @Test
    void create_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any())).thenThrow(new TargetProcessApiException(400, "Bad Request"));
        assertThatThrownBy(() -> service.createRelease("Bad", 0, null, null))
                .isInstanceOf(TargetProcessApiException.class);
    }

    private void givenApiReturns(String body) {
        when(httpClient.post(any(), any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(Release.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body, Release.class));
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






