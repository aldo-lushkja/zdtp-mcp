# Targetprocess MCP Server

A Spring Boot application implementing the [Model Context Protocol (MCP)](https://modelcontextprotocol.io) to expose IBM Targetprocess data to AI assistants.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Build & Run](#build--run)
- [Architecture](#architecture)
- [Data Model](#data-model)
- [Available Tools](#available-tools)
- [Integration](#integration)

---

## Prerequisites

- Java 21+
- Gradle (wrapper included)
- A Targetprocess API Access Token (Profile → Settings → API Access Tokens)

---

## Configuration

Set the following environment variables:

| Variable | Example |
| --- | --- |
| `TARGETPROCESS_BASE_URL` | `https://youraccount.tpondemand.com` |
| `TARGETPROCESS_ACCESS_TOKEN` | Your generated API token |

---

## Build & Run

```bash
# Build fat JAR
./gradlew build --no-daemon

# Run tests
./gradlew test

# Start server (waits for MCP JSON-RPC on stdin)
java -jar build/libs/targetprocess-mcp-0.0.1-SNAPSHOT.jar
```

> The server uses STDIO transport — all logs go to **stderr** so stdout stays clean for MCP communication.

---

## Architecture

### Request flow

```mermaid
sequenceDiagram
    participant AI as AI Assistant
    participant MCP as MCP Layer<br/>(TargetProcessMcpTools)
    participant SVC as Service Layer<br/>(e.g. UserStorySearchService)
    participant HTTP as TargetProcessHttpClient<br/>(Java 21 HttpClient)
    participant TP as Targetprocess API v1

    AI->>MCP: JSON-RPC tool call over STDIO
    MCP->>SVC: Typed method call with parameters
    SVC->>SVC: Build OData WHERE clause + include string
    SVC->>HTTP: fetch(url) / post(url, body)
    HTTP->>TP: HTTP GET/POST + access_token
    TP-->>HTTP: JSON response
    HTTP-->>SVC: Parsed response (TargetProcessResponse<T>)
    SVC->>SVC: Convert model → DTO
    SVC-->>MCP: List<DTO> / DTO
    MCP-->>AI: Formatted string result
```

### Package structure

```
com.ibm.mcp.targetprocess/
├── config/               TargetProcessConfig, TargetProcessProperties
├── shared/
│   ├── client/           TargetProcessHttpClient
│   ├── exception/        TargetProcessApiException, TargetProcessClientException
│   └── model/            Project, Owner, EntityState, ReleaseReference, TargetProcessResponse
├── userstory/            model / dto / converter / service / controller
├── epic/                 model / dto / converter / service / controller
├── feature/              model / dto / converter / service / controller
├── release/              model / dto / converter / service / controller
├── request/              model / dto / converter / service / controller
├── testplan/             model / dto / converter / service / controller
├── testcase/             model / dto / converter / service / controller
└── project/              model / dto / converter / service / controller
```

Each domain package follows the same layered pattern:

```
Tool (@Tool)  →  *SearchService / *CreateService / *UpdateService / *GetByIdService  →  *Converter  →  TargetProcessHttpClient
```

---

## Data Model

The diagram below shows the entities exposed by this server and their relationships within Targetprocess.

```mermaid
erDiagram
    PROJECT ||--o{ EPIC : contains
    PROJECT ||--o{ FEATURE : contains
    PROJECT ||--o{ USER_STORY : contains
    PROJECT ||--o{ RELEASE : contains
    PROJECT ||--o{ REQUEST : contains
    PROJECT ||--o{ TEST_PLAN : contains

    EPIC ||--o{ FEATURE : "parent of"
    FEATURE ||--o{ USER_STORY : "parent of"

    RELEASE ||--o{ USER_STORY : "groups"

    TEST_PLAN ||--o{ TEST_CASE : contains

    PROJECT {
        int id
        string name
    }
    EPIC {
        int id
        string name
        string description
        string state
        string ownerLogin
        double effort
        date createdAt
        date endDate
    }
    FEATURE {
        int id
        string name
        string description
        string state
        string ownerLogin
        double effort
        date createdAt
        date endDate
    }
    USER_STORY {
        int id
        string name
        string description
        string state
        string ownerLogin
        string assigneeLogin
        double effort
        date createdAt
        date endDate
        int releaseId
        string releaseName
    }
    RELEASE {
        int id
        string name
        string description
        string state
        string ownerLogin
        double effort
        date createdAt
        date startDate
        date endDate
    }
    REQUEST {
        int id
        string name
        string description
        string state
        string ownerLogin
        double effort
        date createdAt
    }
    TEST_PLAN {
        int id
        string name
        string description
        string state
        date createdAt
    }
    TEST_CASE {
        int id
        string name
        string description
        string state
        date createdAt
    }
```

---

## Available Tools

| Tool | Entity | Description | Key parameters |
| --- | --- | --- | --- |
| `searchUserStories` | User Story | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `creatorLogin`, `startDate`, `endDate`, `take`, `releaseId` |
| `createUserStory` | User Story | Create a new user story | `name`*, `projectId`*, `description`, `effort` |
| `updateUserStory` | User Story | Update fields by ID (only non-blank fields are changed) | `id`*, `name`, `description`, `stateName`, `effort` |
| `getUserStoryById` | User Story | Fetch full details including description | `id`* |
| `searchEpics` | Epic | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` |
| `createEpic` | Epic | Create a new epic | `name`*, `projectId`*, `description`, `effort` |
| `updateEpic` | Epic | Update fields by ID | `id`*, `name`, `description`, `stateName`, `effort` |
| `getEpicById` | Epic | Fetch full details including description | `id`* |
| `searchFeatures` | Feature | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` |
| `createFeature` | Feature | Create a new feature | `name`*, `projectId`*, `description`, `effort` |
| `updateFeature` | Feature | Update fields by ID | `id`*, `name`, `description`, `stateName`, `effort` |
| `getFeatureById` | Feature | Fetch full details including description | `id`* |
| `searchReleases` | Release | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` |
| `createRelease` | Release | Create a new release | `name`*, `projectId`*, `description`, `effort` |
| `updateRelease` | Release | Update fields by ID | `id`*, `name`, `description`, `stateName`, `effort` |
| `getReleaseById` | Release | Fetch full details including description | `id`* |
| `searchRequests` | Request | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` |
| `createRequest` | Request | Create a new request | `name`*, `projectId`*, `description`, `effort` |
| `updateRequest` | Request | Update fields by ID | `id`*, `name`, `description`, `stateName`, `effort` |
| `getRequestById` | Request | Fetch full details including description | `id`* |
| `searchTestPlans` | Test Plan | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` |
| `createTestPlan` | Test Plan | Create a new test plan | `name`*, `projectId`*, `description` |
| `updateTestPlan` | Test Plan | Update fields by ID | `id`*, `name`, `description`, `stateName` |
| `getTestPlanById` | Test Plan | Fetch full details including description | `id`* |
| `searchTestCases` | Test Case | Search with filters, ordered by creation date desc | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` |
| `createTestCase` | Test Case | Create a new test case | `name`*, `projectId`*, `description`, `testPlanId` |
| `updateTestCase` | Test Case | Update fields by ID | `id`*, `name`, `description`, `stateName` |
| `getTestCaseById` | Test Case | Fetch full details including description | `id`* |
| `searchProjects` | Project | Search projects by name | `nameQuery`, `startDate`, `endDate`, `take` |

`*` required parameter

> **Description format:** always use HTML (`<h2>`, `<p>`, `<ul>`, `<li>`, `<strong>`), never plain markdown. To embed a Mermaid diagram, base64-encode the definition and use `<img src="https://mermaid.ink/img/<base64>" />`.

---

## Integration

### Claude Code CLI

```bash
claude mcp add targetprocess -- java -jar "/path/to/targetprocess-mcp/build/libs/targetprocess-mcp-0.0.1-SNAPSHOT.jar"
```

Or add manually to `~/.claude.json`:

```json
{
  "mcpServers": {
    "targetprocess": {
      "command": "java",
      "args": ["-jar", "/path/to/targetprocess-mcp/build/libs/targetprocess-mcp-0.0.1-SNAPSHOT.jar"],
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
      "args": ["-jar", "/path/to/targetprocess-mcp/build/libs/targetprocess-mcp-0.0.1-SNAPSHOT.jar"],
      "env": {
        "TARGETPROCESS_BASE_URL": "https://youraccount.tpondemand.com",
        "TARGETPROCESS_ACCESS_TOKEN": "your_api_token"
      }
    }
  }
}
```
