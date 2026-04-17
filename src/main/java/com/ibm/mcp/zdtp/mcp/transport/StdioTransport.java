package com.ibm.mcp.zdtp.mcp.transport;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class StdioTransport implements Transport {
    private PrintStream out = System.out;

    @Override
    public void start(Handler handler) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    JsonNode request = handler.parse(line);
                    String response = handler.handle(request);
                    out.println(response);
                    out.flush();
                } catch (Exception e) {
                    System.err.println("Failed to parse or handle request: " + line + " - " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Server loop error: " + e.getMessage());
        }
    }

    @Override
    public void send(String jsonRpc) {
        out.println(jsonRpc);
        out.flush();
    }
}