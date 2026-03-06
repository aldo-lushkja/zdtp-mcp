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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserStorySearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String EMPTY_RESPONSE = "{\"Items\":[]}";
    // 1736899200000 ms epoch = 2025-01-15T00:00:00Z
    // 1739577600000 ms epoch = 2025-02-15T00:00:00Z
    private static final String STORY_RESPONSE = """
            {"Items":[{"Id":42,"Name":"My Story","Project":{"Id":1,"Name":"consumer_loyalty"},
            "EntityState":{"Id":10,"Name":"Open"},"CreateDate":"\\/Date(1736899200000+0000)\\/",
            "EndDate":"\\/Date(1739577600000+0000)\\/","Effort":5.0,
            "Owner":{"Id":5,"Login":"aldo.lushkja"},"AssignedUser":{"Id":6,"Login":"john.doe"}}]}
            """;

    @Mock
    HttpClient httpClient;

    @SuppressWarnings("unchecked")
    @Mock
    HttpResponse<String> httpResponse;

    UserStorySearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(httpClient, new ObjectMapper());
        service = new UserStorySearchService(props, tpHttpClient, new UserStoryConverter());
    }

    // ── URL / where clause tests ────────────────────────────────────────────────

    @Test
    void noFilters_producesEmptyWhereClause() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchUserStories("", "", "", "", "", 10);

        String url = captureDecodedUrl();
        assertThat(url).contains("where=");
        assertThat(urlParam(url, "where")).isEmpty();
    }

    @Test
    void nameFilter_producesNameContainsCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchUserStories("login feature", "", "", "", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Name contains 'login feature'");
    }

    @Test
    void projectFilter_producesProjectNameContainsCondition() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchUserStories("", "consumer_loyalty", "", "", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Project.Name contains 'consumer_loyalty'");
    }

    @Test
    void startDateFilter_usesGteOperator() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchUserStories("", "", "", "2025-01-01", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("CreateDate gte '2025-01-01'");
    }

    @Test
    void endDateFilter_usesLtOperator() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchUserStories("", "", "", "", "2025-01-31", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("CreateDate lt '2025-01-31'");
    }

    @Test
    void ownerLoginFilter_usesOwnerLoginProperty() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchUserStories("", "", "aldo.lushkja", "", "", 10);

        assertThat(urlParam(captureDecodedUrl(), "where"))
                .contains("Owner.Login eq 'aldo.lushkja'");
    }

    @Test
    void allFilters_combinedWithAnd() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchUserStories("story", "consumer_loyalty", "aldo.lushkja", "2025-01-01", "2025-01-31", 10);

        String where = urlParam(captureDecodedUrl(), "where");
        assertThat(where)
                .contains("Name contains 'story'")
                .contains("Project.Name contains 'consumer_loyalty'")
                .contains("Owner.Login eq 'aldo.lushkja'")
                .contains("CreateDate gte '2025-01-01'")
                .contains("CreateDate lt '2025-01-31'")
                .contains(" and ");
    }

    @Test
    void selectClause_doesNotContainCreatedBy() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchUserStories("", "", "", "", "", 10);

        assertThat(captureDecodedUrl()).doesNotContain("CreatedBy");
    }

    @Test
    void selectClause_containsEffortEndDateAndAssignedUser() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchUserStories("", "", "", "", "", 10);

        String url = captureDecodedUrl();
        assertThat(url)
                .contains("Effort")
                .contains("EndDate")
                .contains("AssignedUser");
    }

    @Test
    void takeParameter_isIncludedInUrl() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        service.searchUserStories("", "", "", "", "", 25);

        assertThat(captureDecodedUrl()).contains("take=25");
    }

    // ── Result parsing tests ────────────────────────────────────────────────────

    @Test
    void emptyItemsArray_returnsEmptyList() throws Exception {
        givenApiReturns(EMPTY_RESPONSE);

        List<UserStoryDto> result = service.searchUserStories("", "", "", "", "", 10);

        assertThat(result).isEmpty();
    }

    @Test
    void validResponse_mapsFieldsCorrectly() throws Exception {
        givenApiReturns(STORY_RESPONSE);

        List<UserStoryDto> result = service.searchUserStories("", "", "", "", "", 10);

        assertThat(result).hasSize(1);
        UserStoryDto story = result.get(0);
        assertThat(story.id()).isEqualTo(42);
        assertThat(story.name()).isEqualTo("My Story");
        assertThat(story.projectName()).isEqualTo("consumer_loyalty");
        assertThat(story.state()).isEqualTo("Open");
        assertThat(story.ownerLogin()).isEqualTo("aldo.lushkja");
        assertThat(story.assigneeLogin()).isEqualTo("john.doe");
        assertThat(story.effort()).isEqualTo(5.0);
        assertThat(story.createdAt()).isEqualTo("2025-01-15"); // parsed from /Date(1736899200000+0000)/
        assertThat(story.endDate()).isEqualTo("2025-02-15"); // parsed from /Date(1739577600000+0000)/
    }

    @Test
    void nonOkHttpStatus_throwsTargetProcessApiException() throws Exception {
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("{\"Message\":\"Bad Request\"}");
        doReturn(httpResponse).when(httpClient).send(any(), any());

        assertThatThrownBy(() -> service.searchUserStories("", "", "", "", "", 10))
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

    private String urlParam(String url, String param) {
        return java.util.Arrays.stream(url.split("[?&]"))
                .filter(p -> p.startsWith(param + "="))
                .map(p -> p.substring(param.length() + 1))
                .findFirst()
                .orElse("");
    }
}
