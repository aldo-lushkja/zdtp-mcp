# PRD: Targetprocess MCP Server

## Overview
The **Targetprocess MCP Server** is a Spring Boot-based service that implements the Model Context Protocol (MCP) to expose IBM Targetprocess functionality to AI models. This allows AI assistants (like Claude) to directly interact with Targetprocess data, starting with the ability to search for User Stories.

## Goals
- Provide a standardized interface (MCP) for AI models to query Targetprocess.
- Leverage Spring Boot and Spring AI for a robust, maintainable, and idiomatic Java implementation.
- Enable efficient searching and retrieval of User Stories from Targetprocess.

## Target Audience
- Developers using AI assistants to manage their project workflows in Targetprocess.
- Teams looking to automate or enhance their project management tasks with AI.

## Technical Stack
- **Language:** Java 21 (using modern features like Records and Pattern Matching).
- **Framework:** Spring Boot 3.4+.
- **AI Integration:** Spring AI with MCP Server Starter.
- **API:** IBM Targetprocess REST API v2.
- **Transport:** STDIO (for local use) and SSE (for remote use).

## Requirements

### 1. Targetprocess Integration
- **Search User Stories:**
    - Tool Name: `search_user_stories`
    - Parameters: `query` (String) - Search term for User Story names.
    - Output: List of User Stories with ID, Name, Project, and State.
- **Authentication:**
    - Support for API Token-based authentication via `application.yml` or environment variables.
    - Base URL configuration for the Targetprocess instance (e.g., `https://{account}.tpondemand.com`).

### 2. MCP Server Implementation
- **Tool Discovery:** Expose `search_user_stories` as a discoverable tool for MCP clients.
- **Error Handling:** Robust handling of API errors, network issues, and invalid queries.
- **Logging:** Structured logging for debugging and monitoring.

### 3. Java & Spring Best Practices
- Use **Records** for DTOs and API responses.
- Use **Spring RestClient** for efficient and modern HTTP communication with Targetprocess.
- Use **Configuration Properties** for typed configuration.
- Implement **Health Indicators** (Spring Boot Actuator) to monitor the server status.

## Future Scope (Expansion)
- Search for Epics, Features, and Bugs.
- Update User Story status.
- Create new User Stories or Tasks.
- Retrieve comments and attachments.

## Implementation Plan (Phase 1)
1. Initialize Spring Boot project with Java 21.
2. Add Spring AI MCP Server dependencies.
3. Configure Targetprocess API client.
4. Implement `search_user_stories` tool logic.
5. Verify with an MCP client (e.g., Claude Desktop).
