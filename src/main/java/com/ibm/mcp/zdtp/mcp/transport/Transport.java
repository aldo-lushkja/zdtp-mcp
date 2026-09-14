package com.ibm.mcp.zdtp.mcp.transport;

import com.fasterxml.jackson.databind.JsonNode;

public interface Transport {

    interface Handler {
        JsonNode parse(String json) throws Exception;
        String handle(JsonNode request) throws Exception;
    }

    void start(Handler handler);
}
