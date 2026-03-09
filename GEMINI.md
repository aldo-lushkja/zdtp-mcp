# ♊ Lightweight Targetprocess MCP Server - Gemini Instructions

This document provides instructional context for Gemini CLI when working within the Targetprocess MCP project.

## 🚀 Project Overview
The **Lightweight Targetprocess MCP Server** is a high-performance Java application that implements the [Model Context Protocol (MCP)](https://modelcontextprotocol.io). It acts as a bridge between AI assistants and the Targetprocess API, exposing project management data (User Stories, Tasks, Test Cases, etc.) as actionable tools.

### 🛠️ Tech Stack
- **Language**: Java 21
- **Build System**: Gradle 8.x
- **Main Dependencies**: Jackson (JSON), Commonmark (Markdown), Standard Java HTTP Client.
- **Architecture**: **Boundary-Control-Entity (BCE)** pattern.
- **Key Features**: Zero-framework policy (No Spring/Heavy frameworks), standard multi-stage Docker build, automated GitHub Actions deployment.

## 🔨 Building and Running

### Key Commands
- **Build fat JAR**: `./gradlew shadowJar`
- **Run Tests**: `./gradlew test`
- **Build Docker Image**: `docker build -t zdtp-mcp .`
- **Run Locally (Docker)**:
  ```bash
  docker run -it --rm -e TP_URL="<URL>" -e TP_TOKEN="<TOKEN>" zdtp-mcp
  ```
- **Run Locally (Java)**:
  ```bash
  java -jar build/libs/zdtp-mcp-<version>-all.jar
  ```

### Configuration
The application requires the following environment variables:
- `TP_URL`: Targetprocess instance URL.
- `TP_TOKEN`: API Access Token.

## 🎨 Development Conventions

### Architecture: BCE Pattern
- **Boundary**: Classes in `*.boundary` packages (e.g., `TestCaseMcpTools`). They register tools with the MCP server and handle input/output formatting.
- **Control**: Classes in `*.control` packages (e.g., `TestCaseSearchService`). They contain the business logic and interact with the `QueryEngine`.
- **Entity**: Classes in `*.entity` packages. They define the data structures (POJOs/Records) for API communication.

### Coding Standards
- **Zero Framework Policy**: Use standard Java APIs for HTTP and I/O. Avoid Spring or other heavy frameworks to keep the binary small and startup fast.
- **JSON Mapping**: Use Jackson records for data entities. Use `@JsonProperty` for mapping Targetprocess's PascalCase API fields to Java record components.
- **Error Handling**: Use `TargetProcessApiException` for API-related errors and `TargetProcessClientException` for client-side issues.

### Testing Practices
- **Framework**: JUnit 5 + Mockito + AssertJ.
- **Pattern**: Always mock `TargetProcessHttpClient` in service tests.
- **Verification**: Ensure that `QueryEngine` correctly builds URLs and include strings.

### Tool Registration
New tools must be registered in the appropriate `*McpTools` class within the `register` method using `server.registerTool`.

## 📚 Documentation Reference
- `README.md`: Integration guide for users.
- `docs/TOOLS.md`: Full reference of available tools and parameters.
- `docs/ARCHITECTURE.md`: Detailed design and sequence diagrams.
- `docs/DATA_MODEL.md`: Entity relationship diagrams.
- `docs/DEVELOPMENT.md`: Detailed developer guide.
