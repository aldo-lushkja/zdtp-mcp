package com.ibm.mcp.targetprocess.userstory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.targetprocess.config.TargetProcessProperties;
import com.ibm.mcp.targetprocess.shared.client.TargetProcessHttpClient;
import com.ibm.mcp.targetprocess.shared.exception.TargetProcessApiException;
import com.ibm.mcp.targetprocess.userstory.converter.UserStoryConverter;
import com.ibm.mcp.targetprocess.userstory.dto.UserStoryDto;
import com.ibm.mcp.targetprocess.userstory.model.UserStory;
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
class UserStoryCreateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    private static final String STORY_RESPONSE = """
            {"Id":100,"Name":"New Story","Project":{"Id":42,"Name":"consumer_loyalty"},
            "EntityState":{"Id":10,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "EndDate":null,"Effort":5.0,
            "Owner":{"Id":5,"Login":"aldo.lushkja"},"AssignedUser":null}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    UserStoryCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new UserStoryCreateService(props, httpClient, new UserStoryConverter(), new ObjectMapper());
    }

    // ── URL tests ────────────────────────────────────────────────────────────────

    @Test
    void create_urlPointsToUserStoriesEndpoint() {
        givenApiReturns(STORY_RESPONSE);

        service.createUserStory("New Story", 42, null, null);

        String url = captureUrl();
        assertThat(url).contains(BASE_URL + "/api/v1/UserStories");
    }

    @Test
    void create_urlContainsFormatJson() {
        givenApiReturns(STORY_RESPONSE);

        service.createUserStory("New Story", 42, null, null);

        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void create_urlContainsInclude() {
        givenApiReturns(STORY_RESPONSE);

        service.createUserStory("New Story", 42, null, null);

        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8))
                .contains("AssignedUser");
    }

    // ── Request body tests ───────────────────────────────────────────────────────

    @Test
    void create_bodyContainsNameAndProjectId() {
        givenApiReturns(STORY_RESPONSE);

        service.createUserStory("New Story", 42, null, null);

        String body = captureBody();
        assertThat(body).contains("\"Name\":\"New Story\"");
        assertThat(body).contains("\"Id\":42");
    }

    @Test
    void create_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(STORY_RESPONSE);

        service.createUserStory("New Story", 42, "Some description", null);

        assertThat(captureBody()).contains("\"Description\":\"Some description\"");
    }

    @Test
    void create_bodyOmitsDescriptionWhenBlank() {
        givenApiReturns(STORY_RESPONSE);

        service.createUserStory("New Story", 42, "", null);

        assertThat(captureBody()).doesNotContain("Description");
    }

    @Test
    void create_bodyContainsEffortWhenProvided() {
        givenApiReturns(STORY_RESPONSE);

        service.createUserStory("New Story", 42, null, 8.0);

        assertThat(captureBody()).contains("\"Effort\":8.0");
    }

    @Test
    void create_bodyOmitsEffortWhenNull() {
        givenApiReturns(STORY_RESPONSE);

        service.createUserStory("New Story", 42, null, null);

        assertThat(captureBody()).doesNotContain("Effort");
    }

    // ── Response parsing tests ───────────────────────────────────────────────────

    @Test
    void create_mapsResponseCorrectly() {
        givenApiReturns(STORY_RESPONSE);

        UserStoryDto result = service.createUserStory("New Story", 42, null, null);

        assertThat(result.id()).isEqualTo(100);
        assertThat(result.name()).isEqualTo("New Story");
        assertThat(result.projectName()).isEqualTo("consumer_loyalty");
        assertThat(result.state()).isEqualTo("Open");
        assertThat(result.ownerLogin()).isEqualTo("aldo.lushkja");
        assertThat(result.effort()).isEqualTo(5.0);
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void create_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any()))
                .thenThrow(new TargetProcessApiException(400, "{\"Message\":\"Bad Request\"}"));

        assertThatThrownBy(() -> service.createUserStory("Bad", 0, null, null))
                .isInstanceOf(TargetProcessApiException.class);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private void givenApiReturns(String body) {
        when(httpClient.post(any(), any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(UserStory.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body.trim(), UserStory.class));
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