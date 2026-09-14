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

You can build and run the server locally using Docker (JVM-based or optional GraalVM native).

```bash
# Build the JVM Docker image (default)
docker build -t zdtp-mcp .

# Build the GraalVM Native Image Docker target (optional)
docker build --target native -t zdtp-mcp:native .

# Run locally to verify
docker run -it --rm \
  -e TP_URL="https://youraccount.tpondemand.com" \
  -e TP_TOKEN="your_token" \
  -e TP_TIMEOUT_SECONDS=30 \
  -e TP_MAX_RETRIES=3 \
  -e TP_DEBUG=true \
  zdtp-mcp
```

## ⚙️ Configuration Parameters

| Environment Variable | Description | Default |
| --- | --- | --- |
| `TP_URL` | Targetprocess instance URL | *(Required)* |
| `TP_TOKEN` | Targetprocess API token | *(Required)* |
| `TP_TIMEOUT_SECONDS` | HTTP request timeout in seconds | `30` |
| `TP_MAX_RETRIES` | Max retries for transient HTTP errors (429, 502, 503, 504) | `3` |
| `TP_DEBUG` | Enable verbose HTTP debug logging to stderr | `false` |

## 🧪 Testing

Unit tests are located in `src/test/java`. We use JUnit 5 and Mockito for testing.

```bash
./gradlew test
```

## 🏗️ Technical Details

For more information on the internal workings of the server, please refer to:
- [Architecture](ARCHITECTURE.md)
- [Data Model](DATA_MODEL.md)
