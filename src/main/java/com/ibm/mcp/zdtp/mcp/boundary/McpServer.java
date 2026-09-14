package com.ibm.mcp.zdtp.mcp.boundary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class McpServer {
    public static final String SERVER_VERSION = "1.0.5";
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, ToolDefinition> tools = new ConcurrentHashMap<>();
    private final PrintStream out = System.out;
    private volatile boolean running = true;

    public void registerTool(String name, String description, JsonNode inputSchema, Function<JsonNode, String> handler) {
        tools.put(name, new ToolDefinition(name, description, inputSchema, handler));
    }

    public void start() {
        start("stdio");
    }

    public void start(String transportType) {
        registerShutdownHook();
        com.ibm.mcp.zdtp.mcp.transport.Transport transport;
        if ("http".equalsIgnoreCase(transportType)) {
            transport = new com.ibm.mcp.zdtp.mcp.transport.HttpTransport(httpPort());
        } else {
            transport = new com.ibm.mcp.zdtp.mcp.transport.StdioTransport();
        }

        var handler = new com.ibm.mcp.zdtp.mcp.transport.Transport.Handler() {
            @Override
            public JsonNode parse(String json) throws Exception {
                return mapper.readTree(json);
            }

            @Override
            public String handle(JsonNode request) throws Exception {
                return processRequest(request);
            }
        };

        transport.start(handler);
    }

    private int httpPort() {
        var port = System.getenv("HTTP_PORT");
        return port != null ? Integer.parseInt(port) : 8080;
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            System.err.println("Targetprocess MCP Server shutting down gracefully...");
        }));
    }

    String processRequest(JsonNode req) throws JsonProcessingException {
        String method = req.path("method").asText();
        JsonNode idNode = req.get("id");

        if ("ping".equals(method)) {
            return formatResponse(idNode, mapper.createObjectNode(), null);
        }

        if ("initialize".equals(method)) {
            ObjectNode result = mapper.createObjectNode();
            result.put("protocolVersion", "2024-11-05");
            ObjectNode serverInfo = result.putObject("serverInfo");
            serverInfo.put("name", "zdtp-mcp");
            serverInfo.put("version", SERVER_VERSION);

            ObjectNode capabilities = result.putObject("capabilities");
            capabilities.putObject("tools");

            return formatResponse(idNode, result, null);
        }

        if ("notifications/initialized".equals(method)) {
            return null;
        }

        if ("tools/list".equals(method)) {
            ObjectNode result = mapper.createObjectNode();
            ArrayNode toolsArray = result.putArray("tools");
            for (ToolDefinition t : tools.values()) {
                ObjectNode toolNode = toolsArray.addObject();
                toolNode.put("name", t.name());
                toolNode.put("description", t.description());
                toolNode.set("inputSchema", t.inputSchema());
            }
            return formatResponse(idNode, result, null);
        }

        if ("tools/call".equals(method)) {
            JsonNode params = req.path("params");
            String name = params.path("name").asText();
            JsonNode args = params.path("arguments");

            ToolDefinition tool = tools.get(name);
            ObjectNode result = mapper.createObjectNode();
            ArrayNode content = result.putArray("content");
            ObjectNode contentItem = content.addObject();
            contentItem.put("type", "text");

            if (tool == null) {
                result.put("isError", true);
                contentItem.put("text", "Unknown tool: " + name);
            } else {
                try {
                    String output = tool.handler().apply(args);
                    contentItem.put("text", output);
                } catch (Exception e) {
                    result.put("isError", true);
                    String errorMsg = "Tool execution failed: " + e.getMessage();
                    if (e.getCause() != null) {
                        errorMsg += " (Cause: " + e.getCause().getMessage() + ")";
                    }
                    contentItem.put("text", errorMsg);
                }
            }
            return formatResponse(idNode, result, null);
        }

        if (idNode != null && !idNode.isMissingNode()) {
            ObjectNode error = mapper.createObjectNode();
            error.put("code", -32601);
            error.put("message", "Method not found");
            return formatResponse(idNode, null, error);
        }
        return null;
    }

    private String formatResponse(JsonNode idNode, JsonNode result, JsonNode error) throws JsonProcessingException {
        if (idNode == null || idNode.isMissingNode()) return null;
        ObjectNode response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.set("id", idNode);
        if (error != null) {
            response.set("error", error);
        } else {
            response.set("result", result);
        }
        return mapper.writeValueAsString(response);
    }

    private record ToolDefinition(String name, String description, JsonNode inputSchema, Function<JsonNode, String> handler) {}
}
