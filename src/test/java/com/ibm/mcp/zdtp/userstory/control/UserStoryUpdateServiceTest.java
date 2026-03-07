package com.ibm.mcp.zdtp.userstory.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.exception.TargetProcessApiException;
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
class UserStoryUpdateServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String STORY_RESPONSE = """
            {"Id":123,"Name":"Updated Story","Project":{"Id":1,"Name":"P1"},
            "EntityState":{"Id":2,"Name":"In Dev"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "EndDate":null,"Effort":5.0,
            "Owner":{"Id":5,"Login":"owner@test.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    UserStoryUpdateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new UserStoryUpdateService(engine, new UserStoryConverter());
    }

    @Test
    void update_urlContainsStoryId() {
        givenApiReturns(STORY_RESPONSE);
        service.updateUserStory(123, "New Name", null, null, null);
        assertThat(captureUrl()).contains("/api/v1/UserStories/123");
    }

    @Test
    void update_urlContainsFormatJson() {
        givenApiReturns(STORY_RESPONSE);
        service.updateUserStory(123, "New Name", null, null, null);
        assertThat(captureUrl()).contains("format=json");
    }

    @Test
    void update_urlContainsInclude() {
        givenApiReturns(STORY_RESPONSE);
        service.updateUserStory(123, "New Name", null, null, null);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("AssignedUser");
    }

    @Test
    void update_bodyContainsNameWhenProvided() {
        givenApiReturns(STORY_RESPONSE);
        service.updateUserStory(123, "New Name", null, null, null);
        assertThat(captureBody()).contains("\"Name\":\"New Name\"");
    }

    @Test
    void update_bodyOmitsNameWhenBlank() {
        givenApiReturns(STORY_RESPONSE);
        service.updateUserStory(123, "", "Desc", null, null);
        assertThat(captureBody()).doesNotContain("\"Name\":");
    }

    @Test
    void update_bodyContainsDescriptionWhenProvided() {
        givenApiReturns(STORY_RESPONSE);
        service.updateUserStory(123, null, "New Description", null, null);
        assertThat(captureBody()).contains("\"Description\":\"New Description\"");
    }

    @Test
    void update_bodyContainsEntityStateWhenProvided() {
        givenApiReturns(STORY_RESPONSE);
        service.updateUserStory(123, null, null, "In Dev", null);
        assertThat(captureBody()).contains("\"EntityState\":{\"Name\":\"In Dev\"}");
    }

    @Test
    void update_bodyOmitsEntityStateWhenBlank() {
        givenApiReturns(STORY_RESPONSE);
        service.updateUserStory(123, null, null, "", null);
        assertThat(captureBody()).doesNotContain("EntityState");
    }

    @Test
    void update_bodyContainsEffortWhenProvided() {
        givenApiReturns(STORY_RESPONSE);
        service.updateUserStory(123, null, null, null, 13.0);
        assertThat(captureBody()).contains("\"Effort\":13.0");
    }

    @Test
    void update_emptyBody_whenAllFieldsNull() {
        givenApiReturns(STORY_RESPONSE);
        service.updateUserStory(123, null, null, null, null);
        assertThat(captureBody()).isEqualTo("{}");
    }

    @Test
    void update_mapsResponseCorrectly() {
        givenApiReturns(STORY_RESPONSE);
        UserStoryDto result = service.updateUserStory(123, "Updated Story", null, null, null);
        assertThat(result.id()).isEqualTo(123);
        assertThat(result.name()).isEqualTo("Updated Story");
    }

    @Test
    void update_apiError_throwsTargetProcessApiException() {
        when(httpClient.post(any(), any())).thenThrow(new TargetProcessApiException(400, "Bad Request"));
        assertThatThrownBy(() -> service.updateUserStory(123, "Name", null, null, null))
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






