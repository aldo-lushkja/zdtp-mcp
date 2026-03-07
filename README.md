# 🎯 Zero Dependency Targetprocess MCP Server

A lightweight Java application implementing the [Model Context Protocol (MCP)](https://modelcontextprotocol.io) to expose Targetprocess data to AI assistants. This version is refactored for **zero external framework dependencies**, using standard Java libraries for HTTP, I/O, and logging.

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Build & Run](#build--run)
- [Architecture](#architecture)
- [Data Model](#data-model)
- [Available Tools](#available-tools)
- [Example Prompts](#example-prompts)
- [Integration](#integration)

---

## ✅ Prerequisites

- **Java 21**
- Gradle (wrapper included)
- A Targetprocess API Access Token (Profile → Settings → API Access Tokens)

---

## ⚙️ Configuration

Set the following environment variables:

| Variable | Example |
| --- | --- |
| `TARGETPROCESS_BASE_URL` | `https://youraccount.tpondemand.com` |
| `TARGETPROCESS_ACCESS_TOKEN` | Your generated API token |

---

## 🔨 Build & Run

```bash
# Set JAVA_HOME to Java 21 if not default
# export JAVA_HOME=/path/to/java-21

# Build fat JAR
./gradlew shadowJar

# Run tests
./gradlew test

# Start server (waits for MCP JSON-RPC on stdin)
java -jar build/libs/zdtp-mcp-1.0.0-all.jar
```

> 📌 The server uses **STDIO transport** — all logs go to **stderr** (via `System.err`) so stdout stays clean for MCP communication. No external logging frameworks (SLF4J/Logback) are used.

---

## 🏗️ Architecture

### Request flow

The application follows the **Boundary-Control-Entity (BCE)** pattern, ensuring a clean separation between the MCP interface, business logic, and data entities.

![Request flow](https://mermaid.ink/img/c2VxdWVuY2VEaWFncmFtCiAgICBwYXJ0aWNpcGFudCBBSSBhcyBBSSBBc3Npc3RhbnQKICAgIHBhcnRpY2lwYW50IE1DUCBhcyBNQ1AgTGF5ZXI8YnIvPihUYXJnZXRQcm9jZXNzTWNwVG9vbHMpCiAgICBwYXJ0aWNpcGFudCBTVkMgYXMgU2VydmljZSBMYXllcjxici8+KGUuZy4gVXNlclN0b3J5U2VhcmNoU2VydmljZSkKICAgIHBhcnRpY2lwYW50IEhUVFAgYXMgVGFyZ2V0UHJvY2Vzc0h0dHBDbGllbnQ8YnIvPihKYXZhIDIxIEh0dHBDbGllbnQpCiAgICBwYXJ0aWNpcGFudCBUUCBhcyBUYXJnZXRwcm9jZXNzIEFQSSB2MQoKICAgIEFJLT4+TUNQOiBKU09OLVJQQyB0b29sIGNhbGwgb3ZlciBTVERJTwogICAgTUNQLT4+U1ZDOiBUeXBlZCBtZXRob2QgY2FsbCB3aXRoIHBhcmFtZXRlcnMKICAgIFNWQy0+PlNWQzogQnVpbGQgT0RhdGEgV0hFUkUgY2xhdXNlICsgaW5jbHVkZSBzdHJpbmcKICAgIFNWQy0+PkhUVFA6IGZldGNoKHVybCkgLyBwb3N0KHVybCwgYm9keSkKICAgIEhUVFAtPj5UUDogSFRUUCBHRVQvUE9TVCArIGFjY2Vzc190b2tlbgogICAgVFAtLT4+SFRUUDogSlNPTiByZXNwb25zZQogICAgSFRUUC0tPj5TVkM6IFBhcnNlZCByZXNwb25zZSAoVGFyZ2V0UHJvY2Vzc1Jlc3BvbnNlPFQ+KQogICAgU1ZDLT4+U1ZDOiBDb252ZXJ0IG1vZGVsIHRvIERUTwogICAgU1ZDLS0+Pk1DUDogTGlzdDxEVE8+IC8gRFRPCiAgICBNQ1AtLT4+QUk6IEZvcm1hdHRlZCBzdHJpbmcgcmVzdWx0)

### 📦 Package structure

```
com.ibm.mcp.zdtp/
├── config/               TargetProcessProperties (Manual wiring in ZdtpMcpApplication)
├── shared/
│   ├── control/          TargetProcessHttpClient (Java 21 Native)
│   ├── entity/           Common TP models and response wrappers
│   └── boundary/         Global exceptions
├── [domain]/             e.g., userstory, epic, feature
│   ├── boundary/         McpTools (@Tool definitions)
│   ├── control/          Services and Converters
│   └── entity/           DTOs and Domain Models
└── mcp/
    └── boundary/         McpServer (JSON-RPC handling), SchemaBuilder
```

Each domain package follows the BCE pattern:
- **Boundary**: The tool interface exposed to the AI.
- **Control**: Business logic, URL building, and data transformation.
- **Entity**: Data structures representing Targetprocess objects.

---

## 🗂️ Data Model

The diagram below shows the entities exposed by this server and their relationships within Targetprocess.

![Data model](https://mermaid.ink/img/ZXJEaWFncmFtCiAgICBQUk9KRUNUIHx8LS1veyBFUElDIDogY29udGFpbnMKICAgIFBST0pFQ1QgfHwtLW97IEZFQVRVUkUgOiBjb250YWlucwogICAgUFJPSkVDVCB8fC0tb3sgVVNFUl9TVE9SWSA6IGNvbnRhaW5zCiAgICBQUk9KRUNUIHx8LS1veyBSRUxFQVNFIDogY29udGFpbnMKICAgIFBST0pFQ1QgfHwtLW97IFJFUVVFU1QgOiBjb250YWlucwogICAgUFJPSkVDVCB8fC0tb3sgVEVTVF9QTEFOIDogY29udGFpbnMKCiAgICBFUElDIHx8LS1veyBGRUFUVVJFIDogInBhcmVudCBvZiIKICAgIEZFQVRVUkUgfHwtLW97IFVTRVJX U1RPUlkgOiAicGFyZW50IG9mIgoKICAgIFJFTEVBU0UgfHwtLW97IFVTRVJX U1RPUlkgOiBncm91cHMKICAgIFRFQU1fSVRFUkFUSU9OIHx8LS1veyBVU0VSX1NUT1JZIDogInBsYW5uZWQgaW4iCgogICAgVEVBTSB8fC0tb3sgVEVBTV9JVEVSQVRJT04gOiBjb250YWlucwogICAgVEVTVF9QTEFOIHx8LS1veyBURVNUX0NBU0UgOiBjb250YWlucwoKICAgIFBST0pFQ1QgewogICAgICAgIGludCBpZAogICAgICAgIHN0cmluZyBuYW1lCiAgICB9CiAgICBFUElDIHsKICAgICAgICBpbnQgaWQKICAgICAgICBzdHJpbmcgbmFtZQogICAgICAgIHN0cmluZyBkZXNjcmlwdGlvbgogICAgICAgIHN0cmluZyBzdGF0ZQogICAgICAgIHN0cmluZyBvd25lckxvZ2luCiAgICAgICAgZG91YmxlIGVmZm9ydAogICAgICAgIGRhdGUgY3JlYXRlZEF0CiAgICAgICAgZGF0ZSBlbmREYXRlCiAgICB9CiAgICBGRUFUVVJFIHsKICAgICAgICBpbnQgaWQKICAgICAgICBzdHJpbmcgbmFtZQogICAgICAgIHN0cmluZyBkZXNjcmlwdGlvbgogICAgICAgIHN0cmluZyBzdGF0ZQogICAgICAgIHN0cmluZyBvd25lckxvZ2luCiAgICAgICAgZG91YmxlIGVmZm9ydAogICAgICAgIGRhdGUgY3JlYXRlZEF0CiAgICAgICAgZGF0ZSBlbmREYXRlCiAgICB9CiAgICBVU0VSX1NUT1JZIHsKICAgICAgICBpbnQgaWQKICAgICAgICBzdHJpbmcgbmFtZQogICAgICAgIHN0cmluZyBkZXNjcmlwdGlvbgogICAgICAgIHN0cmluZyBzdGF0ZQogICAgICAgIHN0cmluZyBvd25lckxvZ2luCiAgICAgICAgc3RyaW5nIGFzc2lnbmVlTG9naW4KICAgICAgICBkb3VibGUgZWZmb3J0CiAgICAgICAgZGF0ZSBjcmVhdGVkQXQKICAgICAgICBkYXRlIGVuZERhdGUKICAgICAgICBpbnQgcmVsZWFzZUlkCiAgICAgICAgc3RyaW5nIHJlbGVhc2VOYW1lCiAgICB9CiAgICBSRUxFQVNFIHsKICAgICAgICBpbnQgaWQKICAgICAgICBzdHJpbmcgbmFtZQogICAgICAgIHN0cmluZyBkZXNjcmlwdGlvbgogICAgICAgIHN0cmluZyBzdGF0ZQogICAgICAgIHN0cmluZyBvd25lckxvZ2luCiAgICAgICAgZG91YmxlIGVmZm9ydAogICAgICAgIGRhdGUgY3JlYXRlZEF0CiAgICAgICAgZGF0ZSBzdGFydERhdGUKICAgICAgICBkYXRlIGVuZERhdGUKICAgIH0KICAgIFJFUVVFU1QgewogICAgICAgIGludCBpZAogICAgICAgIHN0cmluZyBuYW1lCiAgICAgICAgc3RyaW5nIGRlc2NyaXB0aW9uCiAgICAgICAgc3RyaW5nIHN0YXRlCiAgICAgICAgc3RyaW5nIG93bmVyTG9naW4KICAgICAgICBkb3VibGUgZWZmb3J0CiAgICAgICAgZGF0ZSBjcmVhdGVkQXQKICAgIH0KICAgIFRFU1RfUExBTiB7CiAgICAgICAgaW50IGlkCiAgICAgICAgc3RyaW5nIG5hbWUKICAgICAgICBzdHJpbmcgZGVzY3JpcHRpb24KICAgICAgICBzdHJpbmcgc3RhdGUKICAgICAgICBkYXRlIGNyZWF0ZWRBdAogICAgfQogICAgVEVTVF9DQVNFIHsKICAgICAgICBpbnQgaWQKICAgICAgICBzdHJpbmcgbmFtZQogICAgICAgIHN0cmluZyBkZXNjcmlwdGlvbgogICAgICAgIHN0cmluZyBzdGF0ZQogICAgICAgIGRhdGUgY3JlYXRlZEF0CiAgICB9CiAgICBURUFNIHsKICAgICAgICBpbnQgaWQKICAgICAgICBzdHJpbmcgbmFtZQogICAgfQogICAgVEVBTV9JVEVSQVRJT04gewogICAgICAgIGludCBpZAogICAgICAgIHN0cmluZyBuYW1lCiAgICAgICAgZGF0ZSBzdGFydERhdGUKICAgICAgICBkYXRlIGVuZERhdGUKICAgICAgICBpbnQgdGVhbUlkCiAgICAgICAgc3RyaW5nIHRlYW1OYW1lCiAgICB9)

---

## 🛠️ Available Tools

| Tool | Entity | Description | Key parameters |
| --- | --- | --- | --- |
| `searchUserStories` | 📖 User Story | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `creatorLogin`, `startDate`, `endDate`, `take`, `releaseId`, `sprintId` (team iteration ID) |
| `createUserStory` | 📖 User Story | Create a new user story | `name`*, `projectId`*, `description`, `effort` |
| `updateUserStory` | 📖 User Story | Update fields by ID (only non-blank fields are changed) | `id`*, `name`, `description`, `stateName`, `effort` |
| `getUserStoryById` | 📖 User Story | Fetch full details including description | `id`* |
| `searchEpics` | 🏔️ Epic | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` |
| `createEpic` | 🏔️ Epic | Create a new epic | `name`*, `projectId`*, `description`, `effort` |
| `updateEpic` | 🏔️ Epic | Update fields by ID | `id`*, `name`, `description`, `stateName`, `effort` |
| `getEpicById` | 🏔️ Epic | Fetch full details including description | `id`* |
| `searchFeatures` | ✨ Feature | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take`, `sprintId` (team iteration ID) |
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

## 💬 Example Prompts

These prompts are designed to be copy-pasted directly into an AI assistant connected to this MCP server. Replace values in `[brackets]` with your own.

### 👥 Team & Sprint

```
Find the latest sprint for the [team name] team and show all releases associated with it.
```

```
What is the current team iteration for [team name]? List all user stories planned in it.
```

```
Show me all team iterations for [team name] in the last 3 months.
```

### 🚀 Releases

```
Find all releases for the [project name] project that started this week.
```

```
Show me all releases in the current sprint for [team name]. Include project name and owner.
```

```
Get the full details of release [release ID], including its description.
```

### 📖 User Stories & Backlog

```
Find all open user stories assigned to [firstname.lastname@company.com] created this month.
```

```
Search for user stories in project [project name] linked to release [release ID].
```

```
Show me the last 20 user stories created in [project name] ordered by creation date.
```

```
Find all user stories in the current team iteration for [team name] that have no release assigned.
```

### ✨ Features & Epics

```
Find all features in project [project name] created since [YYYY-MM-DD].
```

```
Search for features planned in team iteration [team iteration ID].
```

```
Get the full details of epic [epic ID], including its description.
```

### 📝 Planning & Creation

```
Create a user story called "[title]" in project [project ID] with effort [N] points.
Use the following description: [plain text — the assistant will convert it to HTML automatically].
```

```
Create a release called "[version]" in project [project ID] starting [YYYY-MM-DD] and ending [YYYY-MM-DD].
```

```
Update user story [ID]: set the state to "In Progress" and assign [N] story points.
```

### 📊 Reporting

```
List all projects whose name contains "[keyword]".
```

```
Show me all releases across [team name]'s last two sprints. Group them by project.
```

```
Find all user stories created by [firstname.lastname@company.com] between [YYYY-MM-DD] and [YYYY-MM-DD].
```

```
How are story points distributed by author across the last 3 sprints for [team name]?
```

---

## 🔌 Integration

### Claude Code CLI

```bash
claude mcp add targetprocess -- java -jar "/path/to/targetprocess-mcp/build/libs/zdtp-mcp-1.0.0-all.jar"
```

Or add manually to `~/.claude.json`:

```json
{
  "mcpServers": {
    "targetprocess": {
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

### Claude Desktop

Add to `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "targetprocess": {
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