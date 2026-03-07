# 🛠️ Development Guide

This document provides instructions for building, testing, and running the Targetprocess MCP server locally.

## 🔨 Manual Build (Gradle)

The project uses Gradle as its build system.

```bash
# Build the fat JAR
./gradlew shadowJar

# Run tests
./gradlew test

# Start the server (waits for MCP JSON-RPC on stdin)
java -jar build/libs/zdtp-mcp-1.0.0-all.jar
```

## 🐳 Local Docker Development

You can build and run the server locally using Docker (JVM-based).

```bash
# Build the Docker image
docker build -t zdtp-mcp .

# Run locally to verify (waits for MCP JSON-RPC on stdin)
docker run -it --rm \
  -e TP_URL="https://youraccount.tpondemand.com" \
  -e TP_TOKEN="your_token" \
  zdtp-mcp
```

## 🧪 Testing

Unit tests are located in `src/test/java`. We use JUnit 5 and Mockito for testing.

```bash
./gradlew test
```

## 🏗️ Technical Details

For more information on the internal workings of the server, please refer to:
- [Architecture](ARCHITECTURE.md)
- [Data Model](DATA_MODEL.md)
