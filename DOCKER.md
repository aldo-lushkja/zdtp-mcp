# Local Development with Docker

This guide explains how to build and run the project locally using Docker, focusing on creating a native binary with GraalVM.

## 1. Multi-stage Dockerfile

Create a `Dockerfile` in the root directory to build the native image within a container:

```dockerfile
# Stage 1: Build the native binary
FROM ghcr.io/graalvm/native-image-community:21 AS builder

# Install findutils (for xargs) required by gradlew
RUN microdnf install -y findutils

WORKDIR /app
COPY . .

# Fix line endings for gradlew (crucial for Windows users)
RUN tr -d '\r' < gradlew > gradlew_unix && \
    mv gradlew_unix gradlew && \
    chmod +x gradlew

# Build the project first, then compile to native
RUN ./gradlew jar shadowJar nativeCompile --no-daemon

# Stage 2: Create the final runtime image
FROM debian:bookworm-slim

WORKDIR /app

# Copy the native binary from the builder stage
COPY --from=builder /app/build/native/nativeCompile/zdtp-mcp /app/zdtp-mcp

# Ensure the binary is executable
RUN chmod +x /app/zdtp-mcp

# Set the entry point
ENTRYPOINT ["/app/zdtp-mcp"]
```

## 2. Build the Docker Image

Run the following command in your terminal:

```bash
docker build -t zdtp-mcp:local .
```

## 3. Run the Application

You can now run the application as a Docker container:

```bash
docker run --rm zdtp-mcp:local
```

### Configuration with Environment Variables

If your application requires environment variables, you can pass them using the `-e` flag:

```bash
docker run --rm \
  -e TARGETPROCESS_URL=https://your-tp-instance.com \
  -e TARGETPROCESS_TOKEN=your-api-token \
  zdtp-mcp:local
```

## 4. Using Docker Compose (Optional)

Create a `docker-compose.yml` for easier management:

```yaml
version: '3.8'
services:
  zdtp-mcp:
    build: .
    image: zdtp-mcp:local
    environment:
      - TARGETPROCESS_URL=${TARGETPROCESS_URL}
      - TARGETPROCESS_TOKEN=${TARGETPROCESS_TOKEN}
```

Then run:

```bash
docker-compose up
```
