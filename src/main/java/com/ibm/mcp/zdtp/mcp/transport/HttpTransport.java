package com.ibm.mcp.zdtp.mcp.transport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class HttpTransport implements Transport {
    private final int port;
    private final ObjectMapper mapper = new ObjectMapper();
    private Handler handler;

    public HttpTransport(int port) {
        this.port = port;
    }

    @Override
    public void start(Handler handler) {
        this.handler = handler;
        try {
            var server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
            
            server.createContext("/health", new HealthHandler());
            server.createContext("/mcp", new McpHandler());
            
            server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
            server.start();
            System.err.println("HTTP server started on http://127.0.0.1:" + port + "/mcp");
            System.err.println("Health check: curl http://127.0.0.1:" + port + "/health");
        } catch (IOException e) {
            throw new RuntimeException("Failed to start HTTP server on port " + port, e);
        }
    }

    @Override
    public void send(String jsonRpc) {
        // Not used - responses handled differently in HTTP mode
    }

    private void sendJson(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        var bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (var os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            var response = "{\"status\":\"ok\",\"transport\":\"http\"}";
            sendJson(exchange, response, 200);
        }
    }

    private class McpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            
            if ("OPTIONS".equals(method)) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            
            if (!"POST".equals(method)) {
                String response = "{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-32600,\"message\":\"Method not allowed\"}}";
                sendJson(exchange, response, 405);
                return;
            }

            try (InputStream is = exchange.getRequestBody()) {
                String body = new String(is.readAllBytes());
                
                if (body.isBlank()) {
                    String response = "{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-32600,\"message\":\"Invalid Request\"}}";
                    sendJson(exchange, response, 400);
                    return;
                }

                JsonNode request = mapper.readTree(body);
                String jsonRpcResponse = handler.handle(request);
                sendJson(exchange, jsonRpcResponse, 200);
                
            } catch (Exception e) {
                String response = "{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-32603,\"message\":\"Internal error: " + e.getMessage() + "\"}}";
                sendJson(exchange, response, 500);
            }
        }
    }
}