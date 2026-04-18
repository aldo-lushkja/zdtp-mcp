# AGENTS.md

## Java Version

- **Target**: Java 25 (LTS)
- **Current**: Java 25

## Build Commands

- **Build fat JAR**: `./gradlew shadowJar` → output at `build/libs/zdtp-mcp-*-all.jar`
- **Build native** (GraalVM): `./gradlew nativeBuild` → `build/native/zdtp-mcp`
- **Build installer** (jpackage): `./gradlew jpackage` → platform-specific installer
- **Run tests**: `./gradlew test`
- **Run locally**: `./zdtp` or `java -jar build/libs/zdtp-mcp-*-all.jar`
- **Run with HTTP**: `TRANSPORT=http java -jar build/libs/zdtp-mcp-*-all.jar`
- **Docker build**: `docker build -t zdtp-mcp .`

## Architecture

- **Pattern**: BCE (Boundary-Control-Entity)
- **Entry point**: `com.ibm.mcp.zdtp.ZdtpMcpApplication`
- **Zero-framework**: Pure Java 25, no Spring/Quarkus. Only Jackson + commonmark for deps.

## Package Structure

- Each domain (userstory, bug, task, feature, epic, release, etc.) has `boundary/`, `control/`, `entity/` packages
- MCP server entry: `src/main/java/com/ibm/mcp/zdtp/mcp/`
- Shared utilities: `src/main/java/com/ibm/mcp/zdtp/shared/`

## Testing

- Tests in `src/test/java` use JUnit 5 + Mockito
- Run single test: `./gradlew test --tests "TestClassName"` or use IDE

## CI/CD (Git Flow)

- Branch strategy: `main`, `develop`, `release/**`, `feature/**`, `bugfix/**`, `hotfix/**`
- CI runs `./gradlew test && ./gradlew shadowJar` on every push
- Docker multi-platform build (amd64 + arm64) on main, develop, release branches, and tags

## Key Files

- `build.gradle` — build config, version management
- `CHANGELOG.md` — bundled into JAR for `server_changelog` tool
- `src/main/resources/server.properties` — version injected at build time