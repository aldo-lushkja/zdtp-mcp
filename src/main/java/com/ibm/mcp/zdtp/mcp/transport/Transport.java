package com.ibm.mcp.zdtp.mcp.transport;

import com.fasterxml.jackson.databind.JsonNode;

public interface Transport {
    void start(Handler handler);
    void send(String jsonRpc);

    interface Handler {
        JsonNode parse(String json) throws Exception;
        String handle(JsonNode request) throws Exception;
    }
}