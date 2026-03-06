# Targetprocess MCP Server

A Spring Boot application implementing the [Model Context Protocol (MCP)](https://modelcontextprotocol.io) to expose IBM Targetprocess data to AI assistants.

## Prerequisites

- Java 21+
- Gradle (wrapper included)
- A Targetprocess API Access Token (Profile → Settings → API Access Tokens)

## Configuration

Set the following environment variables:

| Variable | Example |
| --- | --- |
| `TARGETPROCESS_BASE_URL` | `https://youraccount.tpondemand.com` |
| `TARGETPROCESS_ACCESS_TOKEN` | Your generated API token |

## Build & Run

```bash
# Build fat JAR
./gradlew build --no-daemon

# Run tests
./gradlew test

# Start server (waits for MCP JSON-RPC on stdin)
java -jar build/libs/targetprocess-mcp-0.0.1-SNAPSHOT.jar
```

> The server uses STDIO transport — all logs go to **stderr** so stdout stays clean for MCP communication.

---

## Available Tools

### `searchUserStories`

Search for user stories in Targetprocess. All parameters are optional.

| Parameter | Type | Description |
| --- | --- | --- |
| `nameQuery` | String | Filter by story name (contains) |
| `projectName` | String | Filter by project name |
| `creatorLogin` | String | Filter by author login |
| `startDate` | String | Created on or after (YYYY-MM-DD) |
| `endDate` | String | Created on or before (YYYY-MM-DD) |
| `take` | int | Max results to return |

**Returns:** Formatted list of user stories — `[ID] Name (Project, State, Author, Created)`

---

## Integration

### Claude Code CLI

```bash
claude mcp add targetprocess -- java -jar "/path/to/targetprocess-mcp/build/libs/targetprocess-mcp-0.0.1-SNAPSHOT.jar"
```

Or add manually to `~/.claude.json`:

```json
{
  "mcpServers": {
    "targetprocess": {
      "command": "java",
      "args": ["-jar", "/path/to/targetprocess-mcp/build/libs/targetprocess-mcp-0.0.1-SNAPSHOT.jar"],
      "env": {
        "TARGETPROCESS_BASE_URL": "https://youraccount.tpondemand.com",
        "TARGETPROCESS_ACCESS_TOKEN": "your_api_token"
      }
    }
  }
}
```

### Claude Desktop

Add to `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "targetprocess": {
      "command": "java",
      "args": ["-jar", "/path/to/targetprocess-mcp/build/libs/targetprocess-mcp-0.0.1-SNAPSHOT.jar"],
      "env": {
        "TARGETPROCESS_BASE_URL": "https://youraccount.tpondemand.com",
        "TARGETPROCESS_ACCESS_TOKEN": "your_api_token"
      }
    }
  }
}
```