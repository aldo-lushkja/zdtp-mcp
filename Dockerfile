# --- Build Stage (JVM) ---
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

# --- Runtime Stage (JVM - Default) ---
FROM eclipse-temurin:21-jre-alpine AS jvm

# Security: Create a non-root user to run the application
RUN addgroup -S mcp && adduser -S mcp -G mcp
USER mcp

WORKDIR /opt/mcp

# Copy the shadow JAR from the builder stage
COPY --from=builder /build/build/libs/*-all.jar app.jar

# Environment variables
ENV TP_URL=""
ENV TP_TOKEN=""
ENV TP_TIMEOUT_SECONDS=30
ENV TP_MAX_RETRIES=3
ENV TP_DEBUG=false

# Run the MCP server
ENTRYPOINT ["java", "-jar", "app.jar"]

# --- Native Build Stage (GraalVM Optional Target) ---
FROM ghcr.io/graalvm/native-image-community:21-muslib AS native-builder

WORKDIR /build
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN tr -d '\r' < gradlew > gradlew_unix && \
    mv gradlew_unix gradlew && \
    chmod +x gradlew

RUN ./gradlew nativeCompile --no-daemon || true

# --- Native Runtime Stage ---
FROM alpine:3.19 AS native

RUN addgroup -S mcp && adduser -S mcp -G mcp
USER mcp

WORKDIR /opt/mcp
COPY --from=native-builder /build/build/native/nativeCompile/zdtp-mcp app

ENV TP_URL=""
ENV TP_TOKEN=""
ENV TP_TIMEOUT_SECONDS=30
ENV TP_MAX_RETRIES=3
ENV TP_DEBUG=false

ENTRYPOINT ["./app"]
