package com.ibm.mcp.zdtp.userstory.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.control.TargetProcessHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserStoryDeleteServiceTest {

    private static final String BASE_URL = "http://test.com";
    private static final String TOKEN = "token";

    @Mock
    private TargetProcessHttpClient httpClient;

    private UserStoryDeleteService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties properties = new TargetProcessProperties(BASE_URL, TOKEN);
        service = new UserStoryDeleteService(properties, httpClient, new ObjectMapper());
    }

    @Test
    void delete_callsApiWithCorrectUrl() {
        service.delete(123);
        verify(httpClient).delete(contains("/UserStories/123"));
    }
}
