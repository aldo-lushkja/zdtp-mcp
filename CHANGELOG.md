# Changelog

All notable changes to **zdtp-mcp** are documented here.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

---

## [1.1.0] - 2026-03-27

### Changed
- Java 25 toolchain (`JavaLanguageVersion.of(25)`); source/target remain Java 21 for compatibility
- `McpServer`: replaced chained `if`/`else` with `switch` expression; extracted `handleInitialize`, `handleToolsList`, `handleToolsCall`, `sendMethodNotFound` private methods; `var` throughout
- `SchemaBuilder`: pattern-matching `switch` in `withDefault()` replaces `instanceof` chain
- `ZdtpMcpApplication`: `var` for all local declarations; HTTP client now uses virtual threads (`Executors.newVirtualThreadPerTaskExecutor()`)
- `TargetProcessProperties`: `Objects.requireNonNullElse` replaces `Optional.ofNullable().orElse()`
- Dependencies: Jackson 2.15 → 2.18.3, JUnit BOM 5.10 → 5.11.4, Mockito 5.11 → 5.14.2, AssertJ 3.24 → 3.26.3
- Docker base image: `eclipse-temurin:21` → `eclipse-temurin:25`
- MCP server version is now read dynamically from the JAR manifest at runtime
- New `server_changelog` MCP tool exposes this changelog from within the server

### Fixed
- CI: Gradle JVM stays on JDK 21; Java 25 toolchain is provisioned separately for compilation (`-Dnet.bytebuddy.experimental=true` added for Mockito on Java 25 JVM)

---

## [1.0.9] - 2026-03-27

### Added
- `relation_delete` tool — delete a relation between two TargetProcess entities

### Fixed
- CI: replaced empty-string Docker tag conditionals with `docker/metadata-action` (fixes spurious empty tags)
- CI: upgraded all GitHub Actions to v5 for Node.js 24 compatibility

---

## [1.0.8] - 2026-03-27

### Fixed
- Release domain: removed invalid `EntityState` field that caused API errors when creating or updating releases

### Changed
- First public release on GitHub

---

## [1.0.7] - 2026-03-09

### Added
- Filled gaps in search filters across all entity types (Epic, Feature, UserStory, Bug, Request, Release, TestPlan, Task)
- Expanded `create` / `update` parameters for most entity types

---

## [1.0.6] - 2026-03-09

### Fixed
- TestCase API — corrected field mappings
- Relations endpoint — fixed response parsing

### Changed
- Added `Team` reference to UserStory create/update payload

---

## [1.0.5] - 2026-03-09

### Added
- User search tool (`user_search`)
- Relation tools (`relation_search`, `relation_link`)
- OData `QueryEngine` — centralised URL/query building replacing per-service ad-hoc construction
- Git Flow CI/CD pipeline with multi-platform Docker builds (amd64 + arm64)
- Local MCP testing script

---

## [1.0.3] - 2026-03-08

### Added
- Core entity domains: Epic, Feature, UserStory, Bug, Request, Release, Task, Comment, TestCase, TestPlan, Team, TeamIteration, Project
- Full CRUD + search tools for all supported entities
- `BaseService` abstraction and `MarkdownConverter` for HTML description fields
- OCI-compliant Docker image published to GitHub Container Registry
