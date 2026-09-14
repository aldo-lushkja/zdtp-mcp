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
        if (args.length > 0) {
            String flag = args[0].trim().toLowerCase();
            if ("--version".equals(flag) || "-v".equals(flag)) {
                System.out.println("zdtp-mcp version " + McpServer.SERVER_VERSION);
                return;
            }
            if ("--help".equals(flag) || "-h".equals(flag)) {
                printUsage();
                return;
            }
        }

        TargetProcessProperties properties = TargetProcessProperties.fromEnv();
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        HttpClient javaHttpClient = HttpClient.newBuilder().build();
        TargetProcessHttpClient tpHttpClient = new TargetProcessHttpClient(javaHttpClient, mapper, properties);
        QueryEngine engine = new QueryEngine(properties, tpHttpClient, mapper);

        McpServer server = new McpServer();
        SchemaBuilder schema = new SchemaBuilder(mapper);

        McpToolsRegistry toolsRegistry = new McpToolsRegistry(engine);
        toolsRegistry.registerAllTools(server, schema);

        server.start();
    }

    private static void printUsage() {
        System.out.println("Targetprocess MCP Server (zdtp-mcp v" + McpServer.SERVER_VERSION + ")");
        System.out.println("Usage: java -jar zdtp-mcp-" + McpServer.SERVER_VERSION + "-all.jar [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -v, --version    Print version and exit");
        System.out.println("  -h, --help       Print help and exit");
        System.out.println();
        System.out.println("Environment Variables:");
        System.out.println("  TP_URL             Targetprocess instance URL (Required)");
        System.out.println("  TP_TOKEN           Targetprocess API token (Required)");
        System.out.println("  TP_TIMEOUT_SECONDS Request timeout in seconds (Default: 30)");
        System.out.println("  TP_MAX_RETRIES     Max retries for transient errors (Default: 3)");
        System.out.println("  TP_DEBUG           Enable verbose stderr logging (Default: false)");
    }
}
