package com.ibm.mcp.zdtp.mcp.boundary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.mcp.zdtp.mcp.transport.HttpTransport;
import com.ibm.mcp.zdtp.mcp.transport.StdioTransport;
import com.ibm.mcp.zdtp.mcp.transport.Transport;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class McpServer {
    private static final String SERVER_VERSION = readVersion();

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, ToolDefinition> tools = new ConcurrentHashMap<>();

    public void registerTool(String name, String description, JsonNode inputSchema, Function<JsonNode, String> handler) {
        tools.put(name, new ToolDefinition(name, description, inputSchema, handler));
    }

    public void start() {
        start("stdio");
    }

    public void start(String transportType) {
        Transport transport;
        if ("http".equals(transportType)) {
            transport = new HttpTransport(httpPort());
        } else {
            transport = new StdioTransport();
        }

        var handler = new Transport.Handler() {
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

    String processRequest(JsonNode req) throws JsonProcessingException {
        var method = req.path("method").asText();
        var idNode = req.get("id");

        if ("notifications/initialized".equals(method)) {
            return "";
        }

        String result;
        switch (method) {
            case "initialize" -> result = handleInitialize();
            case "tools/list" -> result = handleToolsList();
            case "tools/call" -> result = handleToolsCall(req);
            default -> { sendMethodNotFound(idNode); return ""; }
        }
        
        return buildResponse(idNode, result, null);
    }

    private String handleInitialize() throws JsonProcessingException {
        var result = mapper.createObjectNode();
        result.put("protocolVersion", "2024-11-05");
        var serverInfo = result.putObject("serverInfo");
        serverInfo.put("name", "zdtp-mcp");
        serverInfo.put("version", SERVER_VERSION);

        var capabilities = result.putObject("capabilities");
        capabilities.putObject("tools");

        return result.toString();
    }

    private String handleToolsList() throws JsonProcessingException {
        var result = mapper.createObjectNode();
        ArrayNode toolsArray = result.putArray("tools");
        for (ToolDefinition t : tools.values()) {
            var toolNode = toolsArray.addObject();
            toolNode.put("name", t.name());
            toolNode.put("description", t.description());
            toolNode.set("inputSchema", t.inputSchema());
        }
        return result.toString();
    }

    private String handleToolsCall(JsonNode req) throws JsonProcessingException {
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
        return result.toString();
    }

    private void sendMethodNotFound(JsonNode idNode) {
        // For now, just log
    }

    private String buildResponse(JsonNode idNode, String result, String error) throws JsonProcessingException {
        if (idNode == null || idNode.isMissingNode()) return "";
        
        var response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        response.set("id", idNode);
        if (error != null) {
            response.put("error", error);
        } else if (result != null) {
            response.put("result", mapper.readTree(result));
        }
        return response.toString();
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