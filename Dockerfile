# Stage 1: Build the application (JAR and Native)
FROM ghcr.io/graalvm/native-image-community:21 AS builder

# Install findutils (for xargs) required by gradlew
RUN microdnf install -y findutils

WORKDIR /app
COPY . .

# Fix line endings for gradlew (crucial for Windows users)
RUN tr -d '\r' < gradlew > gradlew_unix && \
    mv gradlew_unix gradlew && \
    chmod +x gradlew

# Build both shadow JAR and native binary
RUN ./gradlew shadowJar nativeCompile --no-daemon

# --- JVM-based Runtime Image ---
FROM eclipse-temurin:21-jre-alpine AS jvm
WORKDIR /app
COPY --from=builder /app/build/libs/zdtp-mcp-1.0.0-all.jar /app/zdtp-mcp.jar
# Standard MCP server runs via stdin/stdout
ENTRYPOINT ["java", "-jar", "/app/zdtp-mcp.jar"]

# --- Native Runtime Image (Default) ---
FROM debian:bookworm-slim AS native
WORKDIR /app
COPY --from=builder /app/build/native/nativeCompile/zdtp-mcp /app/zdtp-mcp
RUN chmod +x /app/zdtp-mcp
ENTRYPOINT ["/app/zdtp-mcp"]

# Default target is native
FROM native
