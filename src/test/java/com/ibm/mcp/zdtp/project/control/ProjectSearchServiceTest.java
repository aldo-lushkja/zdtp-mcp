package com.ibm.mcp.zdtp.project.control;

import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.project.entity.ProjectDto;
import com.ibm.mcp.zdtp.project.entity.ProjectData;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectSearchServiceTest {

    private static final String BASE_URL = "https://company.tpondemand.com";
    private static final String TOKEN = "test-token";
    private static final String PROJECTS_RESPONSE = """
            {"Items":[{"Id":1,"Name":"Project 1"},{"Id":2,"Name":"Project 2"}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    ProjectSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new ProjectSearchService(engine, new ProjectConverter());
    }

    @Test
    void search_returnsMappedProjects() {
        when(httpClient.fetch(any())).thenReturn(PROJECTS_RESPONSE);
        when(httpClient.parse(any(), any())).thenCallRealMethod();

        List<ProjectDto> result = service.searchProjects(null, null, null, 10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Project 1");
        assertThat(result.get(1).id()).isEqualTo(2);
    }

    @Test
    void search_buildsCorrectUrl() {
        when(httpClient.fetch(any())).thenReturn(PROJECTS_RESPONSE);
        when(httpClient.parse(any(), any())).thenCallRealMethod();

        service.searchProjects("test", "2024-01-01", "2024-12-31", 5);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).fetch(urlCaptor.capture());
        String url = urlCaptor.getValue();

        assertThat(url).contains("/api/v1/Projects");
        assertThat(url).contains("where=" + httpClient.encode("Name contains 'test' and CreateDate gte '2024-01-01' and CreateDate lt '2024-12-31'"));
        assertThat(url).contains("take=5");
    }
}






