package com.ibm.mcp.zdtp.mcp.transport;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HttpTransport implements Transport {

    private final int port;

    public HttpTransport(int port) {
        this.port = port;
    }

    @Override
    public void start(Handler handler) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            server.createContext("/health", exchange -> {
                String response = "{\"status\":\"UP\"}";
                sendResponse(exchange, 200, response);
            });

            server.createContext("/mcp", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        sendResponse(exchange, 451, "Method Not Allowed");
                        return;
                    }

                    try (InputStream is = exchange.getRequestBody()) {
                        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        var req = handler.parse(body);
                        String resp = handler.handle(req);
                        sendResponse(exchange, 200, resp != null ? resp : "");
                    } catch (Exception e) {
                        sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                    }
                }
            });

            server.setExecutor(null);
            System.err.println("[HTTP] Server listening on http://localhost:" + port);
            server.start();

        } catch (IOException e) {
            System.err.println("[HTTP] Failed to start HTTP server: " + e.getMessage());
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
