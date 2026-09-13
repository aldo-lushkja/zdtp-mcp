package com.ibm.mcp.zdtp.mcp.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatNoException;

@ExtendWith(MockitoExtension.class)
class McpToolsRegistryTest {

    @Mock
    TargetProcessHttpClient httpClient;

    @Test
    void registerAllTools_registersEveryToolSuccessfully() {
        ObjectMapper mapper = new ObjectMapper();
        TargetProcessProperties props = new TargetProcessProperties("https://tp.example.com", "token");
        QueryEngine engine = new QueryEngine(props, httpClient, mapper);
        McpServer server = new McpServer();
        SchemaBuilder schema = new SchemaBuilder(mapper);

        McpToolsRegistry registry = new McpToolsRegistry(engine);

        assertThatNoException().isThrownBy(() -> registry.registerAllTools(server, schema));
    }
}
