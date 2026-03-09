package com.ibm.mcp.zdtp.userstory.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.exception.TargetProcessApiException;
import com.ibm.mcp.zdtp.userstory.control.UserStoryConverter;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;
import com.ibm.mcp.zdtp.userstory.entity.UserStory;
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
            {"Id":123,"Name":"New Story","Project":{"Id":42,"Name":"satispay_plus"},
            "EntityState":{"Id":10,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "EndDate":null,"Effort":5.0,
            "Owner":{"Id":5,"Login":"aldo.lushkja@satispay.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    UserStoryCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new UserStoryCreateService(engine, new UserStoryConverter());
    }

    @Test
    void create_urlPointsToUserStoriesEndpoint() {
        givenApiReturns(STORY_RESPONSE);
        service.createUserStory("New Story", 42, null, null);
        assertThat(captureUrl()).contains("/api/v1/UserStories");
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
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("AssignedUser");
    }

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
        service.createUserStory("New Story", 42, "Story description", null);
        assertThat(captureBody()).contains("\"Description\":\"Story description\"");
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
        service.createUserStory("New Story", 42, null, 13.0);
        assertThat(captureBody()).contains("\"Effort\":13.0");
    }

    @Test
    void create_bodyOmitsEffortWhenNull() {
        givenApiReturns(STORY_RESPONSE);
        service.createUserStory("New Story", 42, null, null);
        assertThat(captureBody()).doesNotContain("Effort");
    }

    @Test
    void create_mapsResponseCorrectly() {
        givenApiReturns(STORY_RESPONSE);
        UserStoryDto result = service.createUserStory("New Story", 42, null, null);
        assertThat(result.id()).isEqualTo(123);
        assertThat(result.name()).isEqualTo("New Story");
        assertThat(result.projectName()).isEqualTo("satispay_plus");
        assertThat(result.state()).isEqualTo("Open");
        assertThat(result.ownerLogin()).isEqualTo("aldo.lushkja@satispay.com");
        assertThat(result.effort()).isEqualTo(5.0);
        assertThat(result.createdAt()).isEqualTo("2025-01-15");
    }

    @Test
    void create_bodyContainsTeamIdWhenProvided() {
        givenApiReturns(STORY_RESPONSE);
        service.create("New Story", 42, null, null, null, null, 163894, null);
        assertThat(captureBody()).contains("\"Team\":{\"Id\":163894}");
    }

    @Test
    void create_bodyOmitsTeamWhenNull() {
        givenApiReturns(STORY_RESPONSE);
        service.create("New Story", 42, null, null, null, null, null, null);
        assertThat(captureBody()).doesNotContain("Team");
    }

    @Test
    void create_bodyContainsReleaseIdWhenProvided() {
        givenApiReturns(STORY_RESPONSE);
        service.create("New Story", 42, null, null, null, null, null, 555);
        assertThat(captureBody()).contains("\"Release\":{\"Id\":555}");
    }

    @Test
    void create_bodyOmitsReleaseWhenNull() {
        givenApiReturns(STORY_RESPONSE);
        service.create("New Story", 42, null, null, null, null, null, null);
        assertThat(captureBody()).doesNotContain("Release");
    }

    @Test
    void create_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any())).thenThrow(new TargetProcessApiException(400, "Bad Request"));
        assertThatThrownBy(() -> service.createUserStory("Bad", 0, null, null))
                .isInstanceOf(TargetProcessApiException.class);
    }

    private void givenApiReturns(String body) {
        when(httpClient.post(any(), any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(UserStory.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body, UserStory.class));
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






