package com.ibm.mcp.targetprocess.userstory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessApiException;
import com.ibm.mcp.targetprocess.userstory.converter.UserStoryConverter;
import com.ibm.mcp.targetprocess.userstory.dto.UserStoryDto;
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
class UserStoryGetByIdServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    // 1739577600000 ms epoch = 2025-02-15T00:00:00Z
    private static final String STORY_RESPONSE = """
            {"Id":212789,"Name":"Migration Story","Description":"<p>Details here</p>",
            "Project":{"Id":1,"Name":"consumer_loyalty"},
            "EntityState":{"Id":10,"Name":"Open"},
            "CreateDate":"\\/Date(1736899200000+0000)\\/",
            "EndDate":"\\/Date(1739577600000+0000)\\/","Effort":3.0,
            "Owner":{"Id":5,"Login":"aldo.lushkja"},"AssignedUser":{"Id":6,"Login":"john.doe"}}
            """;

    @Mock
    HttpClient httpClient;

    @SuppressWarnings("unchecked")
    @Mock
    HttpResponse<String> httpResponse;

    UserStoryGetByIdService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(httpClient, new ObjectMapper());
        service = new UserStoryGetByIdService(props, tpHttpClient, new UserStoryConverter());
    }

    @Test
    void urlContainsEntityId() throws Exception {
        givenApiReturns(STORY_RESPONSE);

        service.getById(212789);

        assertThat(captureDecodedUrl()).contains("/UserStories/212789");
    }

    @Test
    void urlContainsRequiredIncludeFields() throws Exception {
        givenApiReturns(STORY_RESPONSE);

        service.getById(212789);

        String url = captureDecodedUrl();
        assertThat(url)
                .contains("Description")
                .contains("Effort")
                .contains("EndDate")
                .contains("AssignedUser");
    }

    @Test
    void validResponse_mapsFieldsCorrectly() throws Exception {
        givenApiReturns(STORY_RESPONSE);

        UserStoryDto story = service.getById(212789);

        assertThat(story.id()).isEqualTo(212789);
        assertThat(story.name()).isEqualTo("Migration Story");
        assertThat(story.description()).isEqualTo("<p>Details here</p>");
        assertThat(story.projectName()).isEqualTo("consumer_loyalty");
        assertThat(story.state()).isEqualTo("Open");
        assertThat(story.ownerLogin()).isEqualTo("aldo.lushkja");
        assertThat(story.assigneeLogin()).isEqualTo("john.doe");
        assertThat(story.effort()).isEqualTo(3.0);
        assertThat(story.createdAt()).isEqualTo("2025-01-15");
        assertThat(story.endDate()).isEqualTo("2025-02-15");
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
