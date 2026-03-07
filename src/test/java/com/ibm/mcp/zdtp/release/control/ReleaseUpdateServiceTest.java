package com.ibm.mcp.zdtp.release.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessApiException;
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
class ReleaseUpdateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String RELEASE_RESPONSE = """
            {"Id":123,"Name":"Updated Release","Project":{"Id":1,"Name":"P1"},
            "EntityState":{"Id":2,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "StartDate":"\\/Date(1736899200000+0000)\\/",
            "EndDate":null,"Effort":5.0,
            "Owner":{"Id":5,"Login":"owner@test.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    ReleaseUpdateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new ReleaseUpdateService(props, httpClient, new ReleaseConverter(), new ObjectMapper());
    }

    @Test
    void update_urlContainsReleaseId() {
        givenApiReturns(RELEASE_RESPONSE);
        service.updateRelease(123, "New Name", null, null, null);
        assertThat(captureUrl()).contains("/api/v1/Releases/123");
    }

    @Test
    void update_urlContainsFormatJson() {
        givenApiReturns(RELEASE_RESPONSE);
        service.updateRelease(123, "New Name", null, null, null);
        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void update_urlContainsIncludeWithStartDate() {
        givenApiReturns(RELEASE_RESPONSE);
        service.updateRelease(123, "New Name", null, null, null);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("StartDate");
    }

    @Test
    void update_bodyContainsNameWhenProvided() {
        givenApiReturns(RELEASE_RESPONSE);
        service.updateRelease(123, "New Name", null, null, null);
        assertThat(captureBody()).contains("\"Name\":\"New Name\"");
    }

    @Test
    void update_bodyOmitsNameWhenBlank() {
        givenApiReturns(RELEASE_RESPONSE);
        service.updateRelease(123, "", "Desc", null, null);
        assertThat(captureBody()).doesNotContain("\"Name\":");
    }

    @Test
    void update_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(RELEASE_RESPONSE);
        service.updateRelease(123, null, "New Description", null, null);
        assertThat(captureBody()).contains("\"Description\":\"New Description\"");
    }

    @Test
    void update_bodyContainsEntityStateWhenProvided() {
        givenApiReturns(RELEASE_RESPONSE);
        service.updateRelease(123, null, null, "Open", null);
        assertThat(captureBody()).contains("\"EntityState\":{\"Name\":\"Open\"}");
    }

    @Test
    void update_bodyOmitsEntityStateWhenBlank() {
        givenApiReturns(RELEASE_RESPONSE);
        service.updateRelease(123, null, null, "", null);
        assertThat(captureBody()).doesNotContain("EntityState");
    }

    @Test
    void update_bodyContainsEffortWhenProvided() {
        givenApiReturns(RELEASE_RESPONSE);
        service.updateRelease(123, null, null, null, 13.0);
        assertThat(captureBody()).contains("\"Effort\":13.0");
    }

    @Test
    void update_emptyBody_whenAllFieldsNull() {
        givenApiReturns(RELEASE_RESPONSE);
        service.updateRelease(123, null, null, null, null);
        assertThat(captureBody()).isEqualTo("{}");
    }

    @Test
    void update_mapsResponseCorrectly() {
        givenApiReturns(RELEASE_RESPONSE);
        ReleaseDto result = service.updateRelease(123, "Updated Release", null, null, null);
        assertThat(result.id()).isEqualTo(123);
        assertThat(result.name()).isEqualTo("Updated Release");
    }

    @Test
    void update_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any())).thenThrow(new TargetProcessApiException(400, "Bad Request"));
        assertThatThrownBy(() -> service.updateRelease(123, "Name", null, null, null))
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
