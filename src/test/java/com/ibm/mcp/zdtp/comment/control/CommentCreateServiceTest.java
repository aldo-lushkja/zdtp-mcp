package com.ibm.mcp.zdtp.comment.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.comment.entity.Comment;
import com.ibm.mcp.zdtp.comment.entity.CommentDto;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentCreateServiceTest {

    private static final String BASE_URL = "http://test.com";
    private static final String TOKEN = "token";
    private static final String COMMENT_RESPONSE = """
                {
                    "Id": 789,
                    "Description": "Test Comment",
                    "CreateDate": "/Date(1709856000000+0000)/",
                    "Owner": { "Id": 1, "Login": "aldo" },
                    "General": { "Id": 123, "Name": "Entity Name" }
                }
                """;

    @Mock
    private TargetProcessHttpClient httpClient;

    private CommentCreateService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties(BASE_URL, TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        QueryEngine engine = new QueryEngine(props, httpClient, mapper);
        service = new CommentCreateService(engine, new CommentConverter());
    }

    @Test
    void addComment_callsApiAndReturnsDto() {
        givenApiReturns(COMMENT_RESPONSE);

        CommentDto result = service.addComment(123, "Test Comment");

        assertThat(result.id()).isEqualTo(789);
        assertThat(result.text()).isEqualTo("Test Comment");
        assertThat(result.author()).isEqualTo("aldo");
        assertThat(result.entityId()).isEqualTo(123);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(httpClient).post(contains("Comments"), bodyCaptor.capture());
        
        String sentBody = bodyCaptor.getValue();
        assertThat(sentBody).contains("\"General\":{\"Id\":123}");
        assertThat(sentBody).contains("\"Description\":\"Test Comment\"");
    }

    private void givenApiReturns(String body) {
        when(httpClient.post(anyString(), anyString())).thenReturn(body);
        when(httpClient.parseSingle(eq(body), eq(Comment.class)))
                .thenAnswer(inv -> new ObjectMapper().readValue(body, Comment.class));
    }
}
