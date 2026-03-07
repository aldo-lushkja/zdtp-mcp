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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class McpServer {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, ToolDefinition> tools = new ConcurrentHashMap<>();
    private final PrintStream out = System.out;

    public void registerTool(String name, String description, JsonNode inputSchema, Function<JsonNode, String> handler) {
        tools.put(name, new ToolDefinition(name, description, inputSchema, handler));
    }

    public void start() {
        System.out.println("Starting Zero Dependency MCP Server");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    JsonNode request = mapper.readTree(line);
                    handleRequest(request);
                } catch (Exception e) {
                    System.err.println("Failed to parse or handle request: " + line + " - " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Server loop error: " + e.getMessage());
        }
    }

    private void handleRequest(JsonNode req) throws JsonProcessingException {
        String method = req.path("method").asText();
        JsonNode idNode = req.path("id");

        if ("initialize".equals(method)) {
            ObjectNode result = mapper.createObjectNode();
            result.put("protocolVersion", "2024-11-05");
            ObjectNode serverInfo = result.putObject("serverInfo");
            serverInfo.put("name", "zdtp-mcp");
            serverInfo.put("version", "1.0.0");
            
            ObjectNode capabilities = result.putObject("capabilities");
            capabilities.putObject("tools");

            sendResponse(idNode, result, null);
            return;
        }

        if ("notifications/initialized".equals(method)) {
            // Nothing to do
            return;
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
            sendResponse(idNode, result, null);
            return;
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
                    contentItem.put("text", "Tool execution failed: " + e.getMessage());
                }
            }
            sendResponse(idNode, result, null);
            return;
        }

        if (!idNode.isMissingNode()) {
            // Unknown method
            ObjectNode error = mapper.createObjectNode();
            error.put("code", -32601);
            error.put("message", "Method not found");
            sendResponse(idNode, null, error);
        }
    }

    private void sendResponse(JsonNode idNode, JsonNode result, JsonNode error) throws JsonProcessingException {
        if (idNode == null || idNode.isMissingNode()) return;
        ObjectNode response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.set("id", idNode);
        if (error != null) {
            response.set("error", error);
        } else {
            response.set("result", result);
        }
        String json = mapper.writeValueAsString(response);
        // Debug: System.err.println("Out: " + json);
        out.println(json);
        out.flush();
    }

    private record ToolDefinition(String name, String description, JsonNode inputSchema, Function<JsonNode, String> handler) {}
}
