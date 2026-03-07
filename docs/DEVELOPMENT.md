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

You can build and run the server locally using Docker. This creates a small native binary using GraalVM by default.

```bash
# Build the default (native) Docker image
docker build -t zdtp-mcp .

# Build the fast JVM-based image (useful for rapid testing)
docker build -t zdtp-mcp:jvm --target jvm .

# Run locally to verify (waits for MCP JSON-RPC on stdin)
docker run -it --rm \
  -e TARGETPROCESS_BASE_URL="https://youraccount.tpondemand.com" \
  -e TARGETPROCESS_ACCESS_TOKEN="your_token" \
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
