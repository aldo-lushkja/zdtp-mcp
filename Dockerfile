# --- Build Base ---
FROM ghcr.io/graalvm/native-image-community:21 AS base_builder
RUN microdnf install -y findutils
WORKDIR /app
COPY . .
# Fix line endings for gradlew (crucial for Windows users)
RUN tr -d '\r' < gradlew > gradlew_unix && \
    mv gradlew_unix gradlew && \
    chmod +x gradlew

# --- JAR Builder ---
FROM base_builder AS jar_builder
RUN ./gradlew shadowJar --no-daemon

# --- Native Builder ---
FROM base_builder AS native_builder
RUN ./gradlew nativeCompile --no-daemon

# --- JVM-based Runtime Image ---
FROM eclipse-temurin:21-jre-alpine AS jvm
WORKDIR /app
COPY --from=jar_builder /app/build/libs/zdtp-mcp-1.0.0-all.jar /app/zdtp-mcp.jar
ENTRYPOINT ["java", "-jar", "/app/zdtp-mcp.jar"]

# --- Native Runtime Image ---
FROM debian:bookworm-slim AS native
WORKDIR /app
COPY --from=native_builder /app/build/native/nativeCompile/zdtp-mcp /app/zdtp-mcp
RUN chmod +x /app/zdtp-mcp
ENTRYPOINT ["/app/zdtp-mcp"]

# Default target
FROM native
