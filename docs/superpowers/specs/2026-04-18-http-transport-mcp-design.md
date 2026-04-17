# HTTP Transport for MCP Server Design

## Overview

Add HTTP transport option alongside stdio for easier debugging and multi-client support.

## Architecture

**Dual transport mode:**
- `stdio` (default): Original stdin/stdout JSON-RPC
- `http`: Java 25 built-in HTTP server

**Configuration via environment:**
```bash
TRANSPORT=http    # "stdio" or "http" (default: stdio)
HTTP_PORT=8080    # port for HTTP server (default: 8080)
```

## HTTP Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/health` | Health check |
| POST | `/mcp` | JSON-RPC 2.0 endpoint |
| GET | `/mcp` | SSE for notifications (optional) |

## Implementation

### 1. Transport Abstraction

Create `Transport` interface:
```java
interface Transport {
    void start(McpServer server);
    void send(String jsonRpc);
}
```

Two implementations:
- `StdioTransport` - existing stdin/stdout
- `HttpTransport` - new HTTP server

### 2. HttpTransport

```java
public class HttpTransport {
    private final int port;
    
    public void start(McpServer server) {
        var server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", healthHandler);
        server.createContext("/mcp", mcpHandler(server));
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
    }
}
```

### 3. Request/Response Flow

```
HTTP POST /mcp
  → Parse JSON-RPC request
  → Handle via McpServer methods (reused)
  → Return JSON-RPC response
```

### 4. JSON-RPC Handling

Reuse existing McpServer methods:
- `handleInitialize()`
- `handleToolsList()`
- `handleToolsCall()`

Extract request processing to separate method.

## Changes to McpServer

1. Extract `processRequest(JsonNode)` - can be called from both stdio and HTTP
2. Add `sendResponse(String)` - outputs to configured transport
3. Make transport injectable

## Testing

- Unit tests for HTTP handler
- Manual test: `curl -X POST http://localhost:8080/mcp -d '{...}'`

## Security

- No auth for now (localhost only by default)
- Port binding to `127.0.0.1` by default

## Backward Compatibility

- Default remains stdio
- Only activates HTTP when `TRANSPORT=http` env var set