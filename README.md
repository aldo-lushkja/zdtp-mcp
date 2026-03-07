# Targetprocess MCP Server

A Spring Boot application implementing the [Model Context Protocol (MCP)](https://modelcontextprotocol.io) to expose IBM Targetprocess data to AI assistants.

## Prerequisites

- Java 21+
- Gradle (wrapper included)
- A Targetprocess API Access Token (Profile → Settings → API Access Tokens)

## Configuration

Set the following environment variables:

| Variable | Example |
| --- | --- |
| `TARGETPROCESS_BASE_URL` | `https://youraccount.tpondemand.com` |
| `TARGETPROCESS_ACCESS_TOKEN` | Your generated API token |

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

## Available Tools

### User Stories

#### `searchUserStories`

Search for user stories. All filter parameters are optional.

| Parameter | Type | Description |
| --- | --- | --- |
| `nameQuery` | String | Filter by story name (contains) |
| `projectName` | String | Filter by project name (contains) |
| `creatorLogin` | String | Filter by owner login |
| `startDate` | String | Created on or after (YYYY-MM-DD) |
| `endDate` | String | Created before (YYYY-MM-DD) |
| `take` | int | Max results to return |

**Returns:** `[ID] Name (Project, State, Author, Assignee, Points, Created, Done)`

#### `createUserStory`

Create a new user story.

| Parameter | Type | Description |
| --- | --- | --- |
| `name` | String | Story name (required) |
| `projectId` | int | Numeric project ID (required) |
| `description` | String | Story description (optional) |
| `effort` | Double | Story points (optional) |

**Returns:** `Created: [ID] Name (Project, State, ...)`

#### `updateUserStory`

Update an existing user story by ID. Only provided (non-blank) fields are changed.

| Parameter | Type | Description |
| --- | --- | --- |
| `id` | int | Story ID (required) |
| `name` | String | New name (optional) |
| `description` | String | New description (optional) |
| `stateName` | String | Workflow state, e.g. `In Progress` (optional) |
| `effort` | Double | Story points (optional) |

**Returns:** `Updated: [ID] Name (Project, State, ...)`

---

### Features

#### `searchFeatures`

Search for features. All filter parameters are optional.

| Parameter | Type | Description |
| --- | --- | --- |
| `nameQuery` | String | Filter by feature name (contains) |
| `projectName` | String | Filter by project name (contains) |
| `ownerLogin` | String | Filter by owner login |
| `startDate` | String | Created on or after (YYYY-MM-DD) |
| `endDate` | String | Created before (YYYY-MM-DD) |
| `take` | int | Max results to return |

**Returns:** `[ID] Name (Project, State, Owner, Points, Created, Done)`

#### `createFeature`

Create a new feature.

| Parameter | Type | Description |
| --- | --- | --- |
| `name` | String | Feature name (required) |
| `projectId` | int | Numeric project ID (required) |
| `description` | String | Feature description (optional) |
| `effort` | Double | Story points (optional) |

**Returns:** `Created: [ID] Name (Project, State, ...)`

#### `updateFeature`

Update an existing feature by ID. Only provided (non-blank) fields are changed.

| Parameter | Type | Description |
| --- | --- | --- |
| `id` | int | Feature ID (required) |
| `name` | String | New name (optional) |
| `description` | String | New description (optional) |
| `stateName` | String | Workflow state, e.g. `Done` (optional) |
| `effort` | Double | Story points (optional) |

**Returns:** `Updated: [ID] Name (Project, State, ...)`

---

### Test Plans

#### `searchTestPlans`

Search for test plans. All filter parameters are optional.

| Parameter | Type | Description |
| --- | --- | --- |
| `nameQuery` | String | Filter by test plan name (contains) |
| `projectName` | String | Filter by project name (contains) |
| `ownerLogin` | String | Filter by owner login |
| `startDate` | String | Created on or after (YYYY-MM-DD) |
| `endDate` | String | Created before (YYYY-MM-DD) |
| `take` | int | Max results to return |

**Returns:** `[ID] Name (Project, State, Owner, Created)`

#### `createTestPlan`

Create a new test plan.

| Parameter | Type | Description |
| --- | --- | --- |
| `name` | String | Test plan name (required) |
| `projectId` | int | Numeric project ID (required) |
| `description` | String | Test plan description (optional) |

**Returns:** `Created: [ID] Name (Project, State, ...)`

#### `updateTestPlan`

Update an existing test plan by ID. Only provided (non-blank) fields are changed.

| Parameter | Type | Description |
| --- | --- | --- |
| `id` | int | Test plan ID (required) |
| `name` | String | New name (optional) |
| `description` | String | New description (optional) |
| `stateName` | String | Workflow state, e.g. `In Progress` (optional) |

**Returns:** `Updated: [ID] Name (Project, State, ...)`

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