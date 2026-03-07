# 🎯 Zero Dependency Targetprocess MCP Server (v1.0.0)

A lightweight Java application implementing the [Model Context Protocol (MCP)](https://modelcontextprotocol.io) to expose Targetprocess data to AI assistants. This version is refactored for **zero external framework dependencies**, using standard Java libraries for HTTP, I/O, and logging.

---

## 🚀 Quick Start

### 🔌 Claude Code CLI Integration

To add this server directly to Claude Code CLI, use the following command:

```bash
# Using the pre-built fat JAR (v1.0.0)
claude mcp add zdtp -- java -jar "/path/to/targetprocess-mcp/build/libs/zdtp-mcp-1.0.0-all.jar"
```

Or using **Docker** (recommended for a portable, containerized setup):

```bash
# 🏎️ Native distribution (Small, fast startup, recommended)
claude mcp add zdtp -- docker run -i --rm \
  -e TARGETPROCESS_BASE_URL="https://youraccount.tpondemand.com" \
  -e TARGETPROCESS_ACCESS_TOKEN="your_token" \
  ghcr.io/owner/zdtp-mcp:native

# ☕ JVM distribution (Standard JAR, useful for quick testing)
claude mcp add zdtp -- docker run -i --rm \
  -e TARGETPROCESS_BASE_URL="https://youraccount.tpondemand.com" \
  -e TARGETPROCESS_ACCESS_TOKEN="your_token" \
  ghcr.io/owner/zdtp-mcp:jvm
```

### 🐳 Local Docker Development

You can build and run the server locally using Docker. This creates a small native binary using GraalVM.

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

### 🔨 Manual Build (Gradle)

```bash
# Build the fat JAR
./gradlew shadowJar

# Run tests
./gradlew test

# Start the server (waits for MCP JSON-RPC on stdin)
java -jar build/libs/zdtp-mcp-1.0.0-all.jar
```

---

## ⚙️ Configuration

Set the following environment variables:

| Variable | Example | Description |
| --- | --- | --- |
| `TARGETPROCESS_BASE_URL` | `https://youraccount.tpondemand.com` | Your Targetprocess instance URL |
| `TARGETPROCESS_ACCESS_TOKEN` | `your_api_token` | API token (Profile → Settings → API Access Tokens) |

---

## 🗂️ Data Model

The diagram below shows the entities exposed by this server and their relationships within the current implementation.

![Data model](https://mermaid.ink/img/ZXJEaWFncmFtCiAgICBQUk9KRUNUIHx8LS1veyBFUElDIDogY29udGFpbnMKICAgIFBST0pFQ1QgfHwtLW97IEZFQVRVUkUgOiBjb250YWlucwogICAgUFJPSkVDVCB8fC0tb3sgVVNFUl9TVE9SWSA6IGNvbnRhaW5zCiAgICBQUk9KRUNUIHx8LS1veyBSRUxFQVNFIDogY29udGFpbnMKICAgIFBST0pFQ1QgfHwtLW97IFJFUVVFU1QgOiBjb250YWlucwogICAgUFJPSkVDVCB8fC0tb3sgVEVTVF9QTEFOIDogY29udGFpbnMKCiAgICBSRUxFQVNFIHx8LS1veyBVU0VSX1NUT1JZIDogZ3JvdXBzCiAgICBURUFNX0lURVJBVElPTiB8fC0tb3sgVVNFUl9TVE9SWSA6ICJwbGFubmVkIGluIgogICAgVEVBTV9JVEVSQVRJT04gfHwtLW97IEZFQVRVUkUgOiAicGxhbm5lZCBpbiIKICAgIFRFQU0gfHwtLW97IFRFQU1fSVRFUkFUSU9OIDogY29udGFpbnMKICAgIFRFU1RfUExBTiB8fC0tb3sgVEVTVF9DQVNFIDogY29udGFpbnMKCiAgICBQUk9KRUNUIHsKICAgICAgICBpbnQgaWQKICAgICAgICBzdHJpbmcgbmFtZQogICAgfQogICAgRVBJQyB7CiAgICAgICAgaW50IGlkCiAgICAgICAgc3RyaW5nIG5hbWUKICAgICAgICBzdHJpbmcgZGVzY3JpcHRpb24KICAgICAgICBzdHJpbmcgc3RhdGUKICAgICAgICBzdHJpbmcgb3duZXJMb2dpbgogICAgICAgIGRvdWJsZSBlZmZvcnQKICAgICAgICBkYXRlIGNyZWF0ZWRBdAogICAgICAgIGRhdGUgZW5kRGF0ZQogICAgfQogICAgRkVBVFVSRSB7CiAgICAgICAgaW50IGlkCiAgICAgICAgc3RyaW5nIG5hbWUKICAgICAgICBzdHJpbmcgZGVzY3JpcHRpb24KICAgICAgICBzdHJpbmcgc3RhdGUKICAgICAgICBzdHJpbmcgb3duZXJMb2dpbgogICAgICAgIGRvdWJsZSBlZmZvcnQKICAgICAgICBkYXRlIGNyZWF0ZWRBdAogICAgICAgIGRhdGUgZW5kRGF0ZQogICAgICAgIGludCBzcHJpbnRJZAogICAgICAgIHN0cmluZyBzcHJpbnROYW1lCiAgICB9CiAgICBVU0VSX1NUT1JZIHsKICAgICAgICBpbnQgaWQKICAgICAgICBzdHJpbmcgbmFtZQogICAgICAgIHN0cmluZyBkZXNjcmlwdGlvbgogICAgICAgIHN0cmluZyBzdGF0ZQogICAgICAgIHN0cmluZyBvd25lckxvZ2luCiAgICAgICAgc3RyaW5nIGFzc2lnbmVlTG9naW4KICAgICAgICBkb3VibGUgZWZmb3J0CiAgICAgICAgZGF0ZSBjcmVhdGVkQXQKICAgICAgICBkYXRlIGVuZERhdGUKICAgICAgICBpbnQgcmVsZWFzZUlkCiAgICAgICAgc3RyaW5nIHJlbGVhc2VOYW1lCiAgICAgICAgaW50IHNwcmludElkCiAgICAgICAgc3RyaW5nIHNwcmludE5hbWUKICAgIH0KICAgIFJFTEVBU0UgewogICAgICAgIGludCBpZAogICAgICAgIHN0cmluZyBuYW1lCiAgICAgICAgc3RyaW5nIGRlc2NyaXB0aW9uCiAgICAgICAgc3RyaW5nIHN0YXRlCiAgICAgICAgc3RyaW5nIG93bmVyTG9naW4KICAgICAgICBkb3VibGUgZWZmb3J0CiAgICAgICAgZGF0ZSBjcmVhdGVkQXQKICAgICAgICBkYXRlIHN0YXJ0RGF0ZQogICAgICAgIGRhdGUgZW5kRGF0ZQogICAgfQogICAgUkVRVUVTVCB7CiAgICAgICAgaW50IGlkCiAgICAgICAgc3RyaW5nIG5hbWUKICAgICAgICBzdHJpbmcgZGVzY3JpcHRpb24KICAgICAgICBzdHJpbmcgc3RhdGUKICAgICAgICBzdHJpbmcgb3duZXJMb2dpbgogICAgICAgIGRvdWJsZSBlZmZvcnQKICAgICAgICBkYXRlIGNyZWF0ZWRBdAogICAgICAgIGRhdGUgZW5kRGF0ZQogICAgfQogICAgVEVTVF9QTEFOIHsKICAgICAgICBpbnQgaWQKICAgICAgICBzdHJpbmcgbmFtZQogICAgICAgIHN0cmluZyBkZXNjcmlwdGlvbgogICAgICAgIHN0cmluZyBzdGF0ZQogICAgICAgIHN0cmluZyBvd25lckxvZ2luCiAgICAgICAgZGF0ZSBjcmVhdGVkQXQKICAgIH0KICAgIFRFU1RfQ0FTRSB7CiAgICAgICAgaW50IGlkCiAgICAgICAgc3RyaW5nIG5hbWUKICAgICAgICBzdHJpbmcgZGVzY3JpcHRpb24KICAgICAgICBzdHJpbmcgc3RhdGUKICAgICAgICBzdHJpbmcgb3duZXJMb2dpbgogICAgICAgIGRhdGUgY3JlYXRlZEF0CiAgICAgICAgc3RyaW5nIHRlc3RQbGFuTmFtZQogICAgfQogICAgVEVBTSB7CiAgICAgICAgaW50IGlkCiAgICAgICAgc3RyaW5nIG5hbWUKICAgIH0KICAgIFRFQU1fSVRFUkFUSU9OIHsKICAgICAgICBpbnQgaWQKICAgICAgICBzdHJpbmcgbmFtZQogICAgICAgIGRhdGUgc3RhcnREYXRlCiAgICAgICAgZGF0ZSBlbmREYXRlCiAgICAgICAgaW50IHRlYW1JZAogICAgICAgIHN0cmluZyB0ZWFtTmFtZQogICAgfQ==)

---

## 🛠️ Available Tools

| Tool | Entity | Description | Key parameters |
| --- | --- | --- | --- |
| `searchUserStories` | 📖 User Story | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `creatorLogin`, `startDate`, `endDate`, `take`, `releaseId`, `sprintId` |
| `createUserStory` | 📖 User Story | Create a new user story | `name`*, `projectId`*, `description`, `effort` |
| `updateUserStory` | 📖 User Story | Update fields by ID | `id`*, `name`, `description`, `stateName`, `effort` |
| `getUserStoryById` | 📖 User Story | Fetch full details including description | `id`* |
| `searchEpics` | 🏔️ Epic | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` |
| `createEpic` | 🏔️ Epic | Create a new epic | `name`*, `projectId`*, `description`, `effort` |
| `updateEpic` | 🏔️ Epic | Update fields by ID | `id`*, `name`, `description`, `stateName`, `effort` |
| `getEpicById` | 🏔️ Epic | Fetch full details including description | `id`* |
| `searchFeatures` | ✨ Feature | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take`, `sprintId` |
| `createFeature` | ✨ Feature | Create a new feature | `name`*, `projectId`*, `description`, `effort` |
| `updateFeature` | ✨ Feature | Update fields by ID | `id`*, `name`, `description`, `stateName`, `effort` |
| `getFeatureById` | ✨ Feature | Fetch full details including description | `id`* |
| `searchReleases` | 🚀 Release | Search with filters, ordered by release start date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take`, `teamIterationId` |
| `createRelease` | 🚀 Release | Create a new release | `name`*, `projectId`*, `description`, `effort` |
| `updateRelease` | 🚀 Release | Update fields by ID | `id`*, `name`, `description`, `stateName`, `effort` |
| `getReleaseById` | 🚀 Release | Fetch full details including description | `id`* |
| `searchRequests` | 📬 Request | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` |
| `createRequest` | 📬 Request | Create a new request | `name`*, `projectId`*, `description`, `effort` |
| `updateRequest` | 📬 Request | Update fields by ID | `id`*, `name`, `description`, `stateName`, `effort` |
| `getRequestById` | 📬 Request | Fetch full details including description | `id`* |
| `searchTestPlans` | 🧪 Test Plan | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` |
| `createTestPlan` | 🧪 Test Plan | Create a new test plan | `name`*, `projectId`*, `description` |
| `updateTestPlan` | 🧪 Test Plan | Update fields by ID | `id`*, `name`, `description`, `stateName` |
| `getTestPlanById` | 🧪 Test Plan | Fetch full details including description | `id`* |
| `searchTestCases` | 🔬 Test Case | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` |
| `createTestCase` | 🔬 Test Case | Create a new test case | `name`*, `projectId`*, `description`, `testPlanId` |
| `updateTestCase` | 🔬 Test Case | Update fields by ID | `id`*, `name`, `description`, `stateName` |
| `getTestCaseById` | 🔬 Test Case | Fetch full details including description | `id`* |
| `searchProjects` | 📁 Project | Search projects by name | `nameQuery`, `startDate`, `endDate`, `take` |
| `searchTeams` | 👥 Team | Search teams by name | `nameQuery`, `take` |
| `getTeamById` | 👥 Team | Fetch team by ID | `id`* |
| `searchTeamIterations` | 🔄 Team Iteration | Search team iterations by name, teamId, teamName, or date range | `nameQuery`, `teamId`, `teamName`, `startDate`, `endDate`, `take` |
| `getTeamIterationById` | 🔄 Team Iteration | Fetch team iteration by ID | `id`* |

`*` required parameter

> 📝 **Description format:** always use HTML (`<h2>`, `<p>`, `<ul>`, `<li>`, `<strong>`), never plain markdown. To embed a Mermaid diagram, base64-encode the definition and use `<img src="https://mermaid.ink/img/<base64>" />`.

---

## 🏗️ Architecture

The application follows the **Boundary-Control-Entity (BCE)** pattern, ensuring a clean separation between the MCP interface, business logic, and data entities.

![Architecture diagram](https://mermaid.ink/img/c2VxdWVuY2VEaWFncmFtCiAgICBwYXJ0aWNpcGFudCBBSSBhcyBBSSBBc3Npc3RhbnQKICAgIHBhcnRpY2lwYW50IE1DUCBhcyBNQ1AgU2VydmVyIChNY3BTZXJ2ZXIpCiAgICBwYXJ0aWNpcGFudCBCTkQgYXMgQm91bmRhcnkgKERvbWFpbk1jcFRvb2xzKQogICAgcGFydGljaXBhbnQgQ1RMIGFzIENvbnRyb2wgKERvbWFpblNlcnZpY2VzKQogICAgcGFydGljaXBhbnQgSFRUUCBhcyBIdHRwQ2xpZW50IChKYXZhIDIxKQogICAgcGFydGljaXBhbnQgVFAgYXMgVGFyZ2V0cHJvY2VzcyBBUEkKCiAgICBBSS0+Pk1DUDogSlNPTi1SUEMgb3ZlciBTVERJTyAodG9vbHMvY2FsbCkKICAgIE1DUC0+PkJORDogUm91dGUgdG8gVG9vbCBIYW5kbGVyCiAgICBCTkQtPj5DVEw6IEludm9rZSBTZXJ2aWNlIE1ldGhvZAogICAgQ1RMLT4+SFRUUDogdGFyZ2V0UHJvY2Vzc0h0dHBDbGllbnQuZmV0Y2goKQogICAgSFRUUC0+PlRQOiBIVFRQIEdFVC9QT1NUICsgYWNjZXNzX3Rva2VuCiAgICBUUC0tPj5IVFRQOiBKU09OIFJlc3BvbnNlCiAgICBIVFRQLS0+PkNUTDogVGFyZ2V0UHJvY2Vzc1Jlc3BvbnNlPFQ+CiAgICBDVEwtPj5DVEw6IERvbWFpbiBNb2RlbCAtPiBEVE8gbWFwcGluZwogICAgQ1RMLS0+PkJORDogRG9tYWluIERUTwogICAgQk5ELS0+Pk1DUDogVG9vbCBFeGVjdXRpb24gUmVzdWx0CiAgICBNQ1AtLT4+QUk6IEpTT04tUlBDIFJlc3BvbnNlIHJlc3VsdA==)

---

## 💬 Example Prompts

### 👥 Team & Sprint
- *Find the latest sprint for the [team name] team and show all releases associated with it.*
- *What is the current team iteration for [team name]? List all user stories planned in it.*
- *Show me all team iterations for [team name] in the last 3 months.*

### 🚀 Releases
- *Find all releases for the [project name] project that started this week.*
- *Show me all releases in the current sprint for [team name]. Include project name and owner.*

### 📖 User Stories & Backlog
- *Find all open user stories assigned to [firstname.lastname@company.com] created this month.*
- *Find all user stories in the current team iteration for [team name] that have no release assigned.*

### ✨ Features & Epics
- *Find all features in project [project name] created since [YYYY-MM-DD].*
- *Get the full details of epic [epic ID], including its description.*

---

## 🔌 Manual Configuration

### Claude Desktop

Add to `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "zdtp": {
      "command": "java",
      "args": ["-jar", "/path/to/targetprocess-mcp/build/libs/zdtp-mcp-1.0.0-all.jar"],
      "env": {
        "TARGETPROCESS_BASE_URL": "https://youraccount.tpondemand.com",
        "TARGETPROCESS_ACCESS_TOKEN": "your_api_token"
      }
    }
  }
}
```
