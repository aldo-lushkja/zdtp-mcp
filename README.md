# Targetprocess MCP Server

A Spring Boot application implementing the Model Context Protocol (MCP) to interact with IBM Targetprocess.

## Features
- **Search User Stories:** Find user stories by name across projects.
- **Java 21 & Spring Boot 3.4:** Leveraging modern Java features and Spring's latest capabilities.
- **Spring AI MCP:** Seamless integration with AI models using the Model Context Protocol.

---

## Local Development

### 1. Prerequisites
- **Java 21+:** [Adoptium (Temurin)](https://adoptium.net/) is recommended.
- **Gradle:** The project includes a Gradle wrapper (`./gradlew`).

### 2. Configuration
The server requires access to your Targetprocess instance. Create an API Access Token in Targetprocess (Profile -> Settings -> API Access Tokens).

Set the following environment variables:
- `TARGETPROCESS_BASE_URL`: e.g., `https://youraccount.tpondemand.com`
- `TARGETPROCESS_ACCESS_TOKEN`: Your generated API token.

### 3. Build the Project
Compile and package the application into a fat JAR:
```powershell
./gradlew build --no-daemon
```
The resulting JAR will be located at `C:\workspace\mcp\targetprocess-mcp\build\libs\targetprocess-mcp-0.0.1-SNAPSHOT.jar`.

### 4. Running for Testing
Since the server uses STDIO for MCP communication, running it directly will not show traditional logs on `stdout`. All logs are redirected to `stderr`.
```powershell
java -jar build/libs/targetprocess-mcp-0.0.1-SNAPSHOT.jar
```
If successful, the server will wait for MCP JSON-RPC messages on `stdin`.

---

## Claude Code CLI Integration

To use this MCP server with the **Claude Code CLI**, you can add it globally or to a specific project.

### Automatic Setup (Recommended)
Run the following command in your terminal:

```bash
claude mcp add targetprocess -- java -jar "C:\workspace\mcp\targetprocess-mcp\build\libs\targetprocess-mcp-0.0.1-SNAPSHOT.jar"
```
*Note: Replace `C:/path/to/...` with the absolute path to your project.*

### Configuration via Environment Variables
Claude Code will use the environment variables from your shell. Ensure `TARGETPROCESS_BASE_URL` and `TARGETPROCESS_ACCESS_TOKEN` are exported in your `.bashrc`, `.zshrc`, or Windows System Environment Variables.

Alternatively, you can edit your `~/.claude.json` (or `%USERPROFILE%\.claude.json`) manually:

```json
{
  "mcpServers": {
    "targetprocess": {
      "command": "java",
      "args": ["-jar", "C:/path/to/targetprocess-mcp/build/libs/targetprocess-mcp-0.0.1-SNAPSHOT.jar"],
      "env": {
        "TARGETPROCESS_BASE_URL": "https://your_account.tpondemand.com",
        "TARGETPROCESS_ACCESS_TOKEN": "your_api_key"
      }
    }
  }
}
```

---

## Claude Desktop Integration

Add the following to your `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "targetprocess": {
      "command": "java",
      "args": [
        "-jar",
        "C:/path/to/targetprocess-mcp/build/libs/targetprocess-mcp-0.0.1-SNAPSHOT.jar"
      ],
      "env": {
        "TARGETPROCESS_BASE_URL": "https://your_account.tpondemand.com",
        "TARGETPROCESS_ACCESS_TOKEN": "your_api_key"
      }
    }
  }
}
```

---

## Available Tools

### `search_user_stories`
- **Parameters:** `query` (String) - The search term for User Story names.
- **Returns:** A formatted list of User Stories including ID, Name, Project, and Current State.
