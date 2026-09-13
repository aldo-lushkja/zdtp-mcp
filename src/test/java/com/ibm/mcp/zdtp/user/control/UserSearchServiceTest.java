package com.ibm.mcp.zdtp.user.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import com.ibm.mcp.zdtp.user.entity.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSearchServiceTest {

    private static final String RESPONSE = """
            {"Items":[{"Id":1,"FirstName":"Aldo","LastName":"User","Login":"aldo"}]}
            """;

    @Mock
    TargetProcessHttpClient httpClient;

    UserSearchService service;

    @BeforeEach
    void setUp() {
        TargetProcessProperties props = new TargetProcessProperties("https://tp.example.com", "token");
        QueryEngine engine = new QueryEngine(props, httpClient, new ObjectMapper());
        service = new UserSearchService(engine, new UserConverter());
    }

    @Test
    void search_returnsUsers() {
        when(httpClient.fetch(any())).thenReturn(RESPONSE);
        when(httpClient.parse(eq(RESPONSE), any())).thenCallRealMethod();

        List<UserDto> res = service.search("Aldo", 10);

        assertThat(res).hasSize(1);
        assertThat(res.get(0).login()).isEqualTo("aldo");
    }
}
