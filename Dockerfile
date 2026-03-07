# --- Build Stage ---
FROM eclipse-temurin:21-jdk-alpine AS builder

# Install build dependencies
RUN apk add --no-cache findutils

WORKDIR /build

# 1. Copy gradle wrapper and scripts first for better layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Fix line endings for Windows compatibility and make gradlew executable
RUN tr -d '\r' < gradlew > gradlew_unix && \
    mv gradlew_unix gradlew && \
    chmod +x gradlew

# 2. Download dependencies (this layer is cached unless build.gradle changes)
RUN ./gradlew dependencies --no-daemon

# 3. Copy source code and build
COPY src src
RUN ./gradlew shadowJar --no-daemon

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre-alpine AS jvm

# Security: Create a non-root user to run the application
RUN addgroup -S mcp && adduser -S mcp -G mcp
USER mcp

WORKDIR /opt/mcp

# Copy the shadow JAR from the builder stage
# Wildcard ensures version-agnostic copying
COPY --from=builder /build/build/libs/*-all.jar app.jar

# Environment variable placeholders
ENV TP_URL=""
ENV TP_TOKEN=""

# Run the MCP server
ENTRYPOINT ["java", "-jar", "app.jar"]
