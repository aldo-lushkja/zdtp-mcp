package com.ibm.mcp.zdtp.userstory.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.userstory.entity.UserStoryDto;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserStorySearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String STORIES_RESPONSE = """
            {"Items":[{"Id":1,"Name":"Story 1"},{"Id":2,"Name":"Story 2"}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    UserStorySearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new UserStorySearchService(engine, new UserStoryConverter());
    }

    @Test
    void noFilters_producesEmptyWhereClause() {
        givenApiReturns(STORIES_RESPONSE);
        service.searchUserStories(null, null, null, null, null, 10, null, null);
        assertThat(captureUrl()).doesNotContain("where=");
    }

    @Test
    void nameFilter_addsNameContainsCondition() {
        givenApiReturns(STORIES_RESPONSE);
        service.searchUserStories("Fix", null, null, null, null, 10, null, null);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("Name contains 'Fix'");
    }

    @Test
    void projectFilter_addsProjectNameContainsCondition() {
        givenApiReturns(STORIES_RESPONSE);
        service.searchUserStories(null, "Mob", null, null, null, 10, null, null);
        assertThat(URLDecoder.decode(captureUrl(), StandardCharsets.UTF_8)).contains("Project.Name contains 'Mob'");
    }

    @Test
    void search_returnsMappedDtos() {
        givenApiReturns(STORIES_RESPONSE);
        List<UserStoryDto> results = service.searchUserStories(null, null, null, null, null, 10, null, null);
        assertThat(results).hasSize(2);
        assertThat(results.get(0).id()).isEqualTo(1);
    }

    private void givenApiReturns(String body) {
        when(httpClient.fetch(any())).thenReturn(body);
        when(httpClient.parse(eq(body), any())).thenCallRealMethod();
    }

    private String captureUrl() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).fetch(captor.capture());
        return captor.getValue();
    }
}






