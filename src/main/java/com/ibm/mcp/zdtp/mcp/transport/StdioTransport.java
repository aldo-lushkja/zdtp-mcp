package com.ibm.mcp.zdtp.mcp.transport;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StdioTransport implements Transport {

    @Override
    public void start(Handler handler) {
        try (var reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    var requestNode = handler.parse(line);
                    String response = handler.handle(requestNode);
                    if (response != null && !response.isEmpty()) {
                        System.out.println(response);
                    }
                } catch (Exception e) {
                    System.err.println("[STDIO] Error processing request: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("[STDIO] Fatal transport error: " + e.getMessage());
        }
    }
}
