package com.ibm.mcp.targetprocess.release.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.release.converter.ReleaseConverter;
import com.ibm.mcp.targetprocess.release.dto.ReleaseDto;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleaseGetByIdServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    // 1739577600000 ms epoch = 2025-02-15T00:00:00Z
    // 1742860800000 ms epoch = 2025-03-25T00:00:00Z
    private static final String RELEASE_RESPONSE = """
            {"Id":77,"Name":"Release 1.0","Description":"<p>Release notes</p>",
            "Project":{"Id":1,"Name":"satispay_plus"},
            "EntityState":{"Id":10,"Name":"Open"},
            "CreateDate":"\\/Date(1736899200000+0000)\\/",
            "StartDate":"\\/Date(1739577600000+0000)\\/",
            "EndDate":"\\/Date(1742860800000+0000)\\/","Effort":5.0,
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"}}
            """;

    @Mock
    HttpClient httpClient;

    @SuppressWarnings("unchecked")
    @Mock
    HttpResponse<String> httpResponse;

    ReleaseGetByIdService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(httpClient, new ObjectMapper());
        service = new ReleaseGetByIdService(props, tpHttpClient, new ReleaseConverter());
    }

    @Test
    void urlContainsEntityId() throws Exception {
        givenApiReturns(RELEASE_RESPONSE);

        service.getById(77);

        assertThat(captureDecodedUrl()).contains("/Releases/77");
    }

    @Test
    void urlContainsRequiredIncludeFields() throws Exception {
        givenApiReturns(RELEASE_RESPONSE);

        service.getById(77);

        String url = captureDecodedUrl();
        assertThat(url)
                .contains("Description")
                .contains("StartDate")
                .contains("EndDate")
                .contains("Effort");
    }

    @Test
    void validResponse_mapsFieldsCorrectly() throws Exception {
        givenApiReturns(RELEASE_RESPONSE);

        ReleaseDto release = service.getById(77);

        assertThat(release.id()).isEqualTo(77);
        assertThat(release.name()).isEqualTo("Release 1.0");
        assertThat(release.description()).isEqualTo("<p>Release notes</p>");
        assertThat(release.projectName()).isEqualTo("satispay_plus");
        assertThat(release.state()).isEqualTo("Open");
        assertThat(release.ownerLogin()).isEqualTo("aldo.lushkja@satispay.com");
        assertThat(release.effort()).isEqualTo(5.0);
        assertThat(release.createdAt()).isEqualTo("2025-01-15");
        assertThat(release.startDate()).isEqualTo("2025-02-15");
        assertThat(release.endDate()).isEqualTo("2025-03-25");
    }

    @Test
    void nonOkHttpStatus_throwsTargetProcessApiException() throws Exception {
        when(httpResponse.statusCode()).thenReturn(404);
        when(httpResponse.body()).thenReturn("{\"Message\":\"Not Found\"}");
        doReturn(httpResponse).when(httpClient).send(any(), any());

        assertThatThrownBy(() -> service.getById(999))
                .isInstanceOf(TargetProcessApiException.class);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private void givenApiReturns(String body) throws IOException, InterruptedException {
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(body);
        doReturn(httpResponse).when(httpClient).send(any(), any());
    }

    private String captureDecodedUrl() throws IOException, InterruptedException {
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture(), any());
        return URLDecoder.decode(captor.getValue().uri().toString(), StandardCharsets.UTF_8);
    }
}
