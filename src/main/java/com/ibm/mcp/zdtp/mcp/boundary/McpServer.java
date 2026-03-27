package com.ibm.mcp.zdtp.mcp.boundary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class McpServer {
    private static final String SERVER_VERSION = readVersion();

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, ToolDefinition> tools = new ConcurrentHashMap<>();
    private final PrintStream out = System.out;

    public void registerTool(String name, String description, JsonNode inputSchema, Function<JsonNode, String> handler) {
        tools.put(name, new ToolDefinition(name, description, inputSchema, handler));
    }

    public void start() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    JsonNode request = mapper.readTree(line);
                    handleRequest(request);
                } catch (Exception e) {
                    // Log errors to stderr, never to stdout as it breaks the protocol
                    System.err.println("Failed to parse or handle request: " + line + " - " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Server loop error: " + e.getMessage());
        }
    }

    private void handleRequest(JsonNode req) throws JsonProcessingException {
        var method = req.path("method").asText();
        var idNode = req.get("id");

        switch (method) {
            case "initialize"                -> handleInitialize(idNode);
            case "notifications/initialized" -> {}
            case "tools/list"                -> handleToolsList(idNode);
            case "tools/call"                -> handleToolsCall(req, idNode);
            default                          -> sendMethodNotFound(idNode);
        }
    }

    private void handleInitialize(JsonNode idNode) throws JsonProcessingException {
        var result = mapper.createObjectNode();
        result.put("protocolVersion", "2024-11-05");
        var serverInfo = result.putObject("serverInfo");
        serverInfo.put("name", "zdtp-mcp");
        serverInfo.put("version", SERVER_VERSION);

        var capabilities = result.putObject("capabilities");
        capabilities.putObject("tools");

        sendResponse(idNode, result, null);
    }

    private void handleToolsList(JsonNode idNode) throws JsonProcessingException {
        var result = mapper.createObjectNode();
        ArrayNode toolsArray = result.putArray("tools");
        for (ToolDefinition t : tools.values()) {
            var toolNode = toolsArray.addObject();
            toolNode.put("name", t.name());
            toolNode.put("description", t.description());
            toolNode.set("inputSchema", t.inputSchema());
        }
        sendResponse(idNode, result, null);
    }

    private void handleToolsCall(JsonNode req, JsonNode idNode) throws JsonProcessingException {
        var params = req.path("params");
        var name = params.path("name").asText();
        var args = params.path("arguments");

        ToolDefinition tool = tools.get(name);
        var result = mapper.createObjectNode();
        ArrayNode content = result.putArray("content");
        var contentItem = content.addObject();
        contentItem.put("type", "text");

        if (tool == null) {
            result.put("isError", true);
            contentItem.put("text", "Unknown tool: " + name);
        } else {
            try {
                var output = tool.handler().apply(args);
                contentItem.put("text", output);
            } catch (Exception e) {
                result.put("isError", true);
                var errorMsg = "Tool execution failed: " + e.getMessage();
                if (e.getCause() != null) {
                    errorMsg += " (Cause: " + e.getCause().getMessage() + ")";
                }
                contentItem.put("text", errorMsg);
            }
        }
        sendResponse(idNode, result, null);
    }

    private void sendMethodNotFound(JsonNode idNode) throws JsonProcessingException {
        if (idNode == null || idNode.isMissingNode()) return;
        var error = mapper.createObjectNode();
        error.put("code", -32601);
        error.put("message", "Method not found");
        sendResponse(idNode, null, error);
    }

    private void sendResponse(JsonNode idNode, JsonNode result, JsonNode error) throws JsonProcessingException {
        if (idNode == null || idNode.isMissingNode()) return;
        var response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.set("id", idNode);
        if (error != null) {
            response.set("error", error);
        } else {
            response.set("result", result);
        }
        var json = mapper.writeValueAsString(response);
        // Debug: System.err.println("Out: " + json);
        out.println(json);
        out.flush();
    }

    private static String readVersion() {
        try (var is = McpServer.class.getResourceAsStream("/server.properties")) {
            if (is == null) return "unknown";
            var props = new Properties();
            props.load(is);
            return props.getProperty("server.version", "unknown");
        } catch (Exception ignored) {
            return "unknown";
        }
    }

    private record ToolDefinition(String name, String description, JsonNode inputSchema, Function<JsonNode, String> handler) {}
}
