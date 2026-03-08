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
class UserStoryGetByIdServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String STORY_RESPONSE = """
            {"Id":123,"Name":"Test Story","Project":{"Id":1,"Name":"P1"},
            "EntityState":{"Id":2,"Name":"In Dev"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "EndDate":null,"Effort":5.0,
            "Owner":{"Id":5,"Login":"owner@test.com"}}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    UserStoryGetByIdService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new UserStoryGetByIdService(engine, new UserStoryConverter());
    }

    @Test
    void urlContainsEntityId() {
        givenApiReturns(STORY_RESPONSE);
        service.getById(123);
        assertThat(captureUrl()).contains("/api/v1/UserStories/123");
    }

    @Test
    void urlContainsRequiredIncludeFields() {
        givenApiReturns(STORY_RESPONSE);
        service.getById(123);
        String url = captureUrl();
        assertThat(url).contains("include=");
        assertThat(URLDecoder.decode(url, StandardCharsets.UTF_8)).contains("AssignedUser");
    }

    @Test
    void validResponse_mapsFieldsCorrectly() {
        givenApiReturns(STORY_RESPONSE);
        UserStoryDto result = service.getById(123);
        assertThat(result.id()).isEqualTo(123);
        assertThat(result.name()).isEqualTo("Test Story");
        assertThat(result.ownerLogin()).isEqualTo("owner@test.com");
    }

    @Test
    void nonOkHttpStatus_throwsTargetProcessApiException() {
        when(httpClient.fetch(any())).thenThrow(new TargetProcessApiException(404, "Not Found"));
        assertThatThrownBy(() -> service.getById(999)).isInstanceOf(TargetProcessApiException.class);
    }

    private void givenApiReturns(String body) {
        when(httpClient.fetch(any())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(UserStory.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body, UserStory.class));
    }

    private String captureUrl() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).fetch(captor.capture());
        return captor.getValue();
    }
}






