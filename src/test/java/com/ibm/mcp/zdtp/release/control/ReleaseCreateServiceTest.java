package com.ibm.mcp.zdtp.release.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.release.control.ReleaseConverter;
import com.ibm.mcp.zdtp.release.entity.ReleaseDto;
import com.ibm.mcp.zdtp.release.entity.Release;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.control.TargetProcessApiException;
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
            {"Id":77,"Name":"Release 1.0","Project":{"Id":42,"Name":"satispay_plus"},
            "EntityState":{"Id":10,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "StartDate":null,"EndDate":null,"Effort":5.0,
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    ReleaseCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new ReleaseCreateService(props, httpClient, new ReleaseConverter(), new ObjectMapper());
    }

    // ── URL tests ────────────────────────────────────────────────────────────────

    @Test
    void create_urlPointsToReleasesEndpoint() {
        givenApiReturns(RELEASE_RESPONSE);

        service.createRelease("Release 1.0", 42, null, null);

        assertThat(captureUrl()).contains(BASE_URL + "/api/v1/Releases");
    }

    @Test
    void create_urlContainsFormatJson() {
        givenApiReturns(RELEASE_RESPONSE);

        service.createRelease("Release 1.0", 42, null, null);

        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void create_urlContainsIncludeWithStartDate() {
        givenApiReturns(RELEASE_RESPONSE);

        service.createRelease("Release 1.0", 42, null, null);

        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8))
                .contains("StartDate");
    }

    // ── Request body tests ───────────────────────────────────────────────────────

    @Test
    void create_bodyContainsNameAndProjectId() {
        givenApiReturns(RELEASE_RESPONSE);

        service.createRelease("Release 1.0", 42, null, null);

        String body = captureBody();
        assertThat(body).contains("\"Name\":\"Release 1.0\"");
        assertThat(body).contains("\"Id\":42");
    }

    @Test
    void create_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(RELEASE_RESPONSE);

        service.createRelease("Release 1.0", 42, "Release description", null);

        assertThat(captureBody()).contains("\"Description\":\"Release description\"");
    }

    @Test
    void create_bodyOmitsDescriptionWhenBlank() {
        givenApiReturns(RELEASE_RESPONSE);

        service.createRelease("Release 1.0", 42, "", null);

        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_bodyContainsEffortWhenProvided() {
        givenApiReturns(RELEASE_RESPONSE);

        service.createRelease("Release 1.0", 42, null, 5.0);

        assertThat(captureBody()).contains("\"Effort\":5.0");
    }

    @Test
    void create_bodyOmitsEffortWhenNull() {
        givenApiReturns(RELEASE_RESPONSE);

        service.createRelease("Release 1.0", 42, null, null);

        assertThat(captureBody()).doesNotContain("Effort");
    }

    // ── Response parsing tests ───────────────────────────────────────────────────

    @Test
    void create_mapsResponseCorrectly() {
        givenApiReturns(RELEASE_RESPONSE);

        ReleaseDto result = service.createRelease("Release 1.0", 42, null, null);

        assertThat(result.id()).isEqualTo(77);
        assertThat(result.name()).isEqualTo("Release 1.0");
        assertThat(result.projectName()).isEqualTo("satispay_plus");
        assertThat(result.state()).isEqualTo("Open");
        assertThat(result.ownerLogin()).isEqualTo("aldo.lushkja@satispay.com");
        assertThat(result.effort()).isEqualTo(5.0);
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void create_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any()))
                .thenThrow(new TargetProcessApiException(400, "{\"Message\":\"Bad Request\"}"));

        assertThatThrownBy(() -> service.createRelease("Bad", 0, null, null))
                .isInstanceOf(TargetProcessApiException.class);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private void givenApiReturns(String body) {
        when(httpClient.post(any(), any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(Release.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body.trim(), Release.class));
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
