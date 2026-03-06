# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build (produces fat JAR)
./gradlew build --no-daemon

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.ibm.mcp.targetprocess.service.TargetProcessServiceTest"

# Run a single test method
./gradlew test --tests "com.ibm.mcp.targetprocess.service.TargetProcessServiceTest.methodName"

# Run the server (STDIO mode — waits for MCP JSON-RPC on stdin)
java -jar build/libs/targetprocess-mcp-0.0.1-SNAPSHOT.jar
```

Required environment variables:
- `TARGETPROCESS_BASE_URL` — e.g. `https://youraccount.tpondemand.com`
- `TARGETPROCESS_ACCESS_TOKEN` — API token from Targetprocess profile settings

## Architecture

This is a **Spring Boot MCP Server** running in STDIO transport mode (no HTTP server). It exposes Targetprocess as tools to AI assistants via the Model Context Protocol.

### Request flow

```
AI model → MCP JSON-RPC over STDIO
         → TargetProcessMcpTools  (tool definitions, Spring AI @Tool annotations)
         → TargetProcessService   (builds OData WHERE clauses, calls Targetprocess REST API v2)
         → Java 21 HttpClient
         → Targetprocess API
```

### Key components

- **`TargetProcessMcpTools`** (`controller/`) — Declares MCP tools using Spring AI annotations. Currently exposes `search_user_stories` with parameters: `nameQuery`, `projectName`, `creatorLogin`, `startDate`, `endDate`, `take`.
- **`TargetProcessService`** (`service/`) — Builds OData-style WHERE clause strings dynamically, calls the API, parses JSON with Jackson, converts `UserStory` → `UserStoryDto` via `UserStoryConverter`.
- **`TargetProcessProperties`** (`config/`) — `@ConfigurationProperties` record binding the two env vars.
- **`model/`** — Raw API response POJOs (`UserStory`, `Project`, `Owner`, `EntityState`, `TargetProcessResponse<T>`).
- **`dto/`** — `UserStoryDto` returned to the tool layer.
- **`exception/`** — `TargetProcessApiException` (non-2xx HTTP), `TargetProcessClientException` (network/parse errors).

### STDIO transport constraint

All logging goes to **stderr** (configured in `logback-spring.xml`) so that stdout remains clean for MCP JSON-RPC messages. This is critical — do not add any stdout writes.

### Testing

Tests live in `src/test/java/.../service/TargetProcessServiceTest.java` and use JUnit 5 + Mockito to mock `HttpClient`. The test pattern is: mock `HttpClient.send()` → assert the constructed request URI and/or parsed response.