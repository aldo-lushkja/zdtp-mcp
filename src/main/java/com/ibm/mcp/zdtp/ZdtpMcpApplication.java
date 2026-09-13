package com.ibm.mcp.zdtp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ibm.mcp.zdtp.mcp.boundary.McpServer;
import com.ibm.mcp.zdtp.mcp.boundary.McpToolsRegistry;
import com.ibm.mcp.zdtp.mcp.boundary.SchemaBuilder;
import com.ibm.mcp.zdtp.shared.config.TargetProcessProperties;
import com.ibm.mcp.zdtp.shared.http.TargetProcessHttpClient;
import com.ibm.mcp.zdtp.shared.odata.QueryEngine;

import java.net.http.HttpClient;

public class ZdtpMcpApplication {

    public static void main(String[] args) {
        TargetProcessProperties properties = TargetProcessProperties.fromEnv();
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        HttpClient javaHttpClient = HttpClient.newBuilder().build();
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(javaHttpClient, mapper);
        QueryEngine engine = new QueryEngine(properties, tpHttpClient, mapper);

        McpServer server = new McpServer();
        SchemaBuilder schema = new SchemaBuilder(mapper);

        McpToolsRegistry toolsRegistry = new McpToolsRegistry(engine);
        toolsRegistry.registerAllTools(server, schema);

        server.start();
    }
}
