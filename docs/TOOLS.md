# 🛠️ Available Tools Reference

This document provides a detailed reference for all tools exposed by the Targetprocess MCP server.

## 📖 User Stories
| Tool | Description | Parameters |
| --- | --- | --- |
| `user_story_search` | Search for user stories. | `nameQuery`, `projectName`, `creatorLogin`, `startDate`, `endDate`, `releaseId`, `sprintId`, `take` (default: 10) |
| `user_story_create` | Create a new user story. | `name`*, `projectId`*, `description`, `effort` |
| `user_story_update` | Update an existing user story. | `id`*, `name`, `description`, `stateName`, `effort` |
| `user_story_get` | Get user story details. | `id`* |
| `user_story_delete` | Delete a user story. | `id`* |

## 📋 Tasks
| Tool | Description | Parameters |
| --- | --- | --- |
| `task_search` | Search for tasks. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `userStoryId`, `take` (default: 10) |
| `task_create` | Create a new task under a user story. | `name`*, `projectId`*, `userStoryId`*, `description` |
| `task_update` | Update an existing task. | `id`*, `name`, `description`, `stateName` |
| `task_get` | Get task details. | `id`* |
| `task_delete` | Delete a task. | `id`* |

## 🏔️ Epics
| Tool | Description | Parameters |
| --- | --- | --- |
| `epic_search` | Search for epics. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` (default: 10) |
| `epic_create` | Create a new epic. | `name`*, `projectId`*, `description`, `effort` |
| `epic_update` | Update an existing epic. | `id`*, `name`, `description`, `stateName`, `effort` |
| `epic_get` | Get epic details. | `id`* |

## ✨ Features
| Tool | Description | Parameters |
| --- | --- | --- |
| `feature_search` | Search for features. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` (default: 10), `sprintId` |
| `feature_create` | Create a new feature. | `name`*, `projectId`*, `description`, `effort` |
| `feature_update` | Update an existing feature. | `id`*, `name`, `description`, `stateName`, `effort` |
| `feature_get` | Get feature details. | `id`* |

## 🐛 Bugs
| Tool | Description | Parameters |
| --- | --- | --- |
| `bug_search` | Search for bugs. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `userStoryId`, `featureId`, `take` (default: 10) |
| `bug_create` | Create a new bug. | `name`*, `projectId`*, `description`, `effort`, `userStoryId`, `featureId` |
| `bug_update` | Update an existing bug. | `id`*, `name`, `description`, `stateName`, `effort` |
| `bug_get` | Get bug details. | `id`* |
| `bug_delete` | Delete a bug. | `id`* |

## 🚀 Releases
| Tool | Description | Parameters |
| --- | --- | --- |
| `release_search` | Search for releases. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` (default: 10), `teamIterationId` |
| `release_create` | Create a new release. | `name`*, `projectId`*, `description`, `effort` |
| `release_update` | Update an existing release. | `id`*, `name`, `description`, `stateName`, `effort` |
| `release_get` | Get release details. | `id`* |

## 📬 Requests
| Tool | Description | Parameters |
| --- | --- | --- |
| `request_search` | Search for requests. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` (default: 10) |
| `request_create` | Create a new request. | `name`*, `projectId`*, `description`, `effort` |
| `request_update` | Update an existing request. | `id`*, `name`, `description`, `stateName`, `effort` |
| `request_get` | Get request details. | `id`* |

## 🧪 Quality Assurance (Test Plans & Cases)
| Tool | Description | Parameters |
| --- | --- | --- |
| `test_plan_search` | Search for test plans. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` (default: 10) |
| `test_plan_create` | Create a new test plan. | `name`*, `projectId`*, `description` |
| `test_plan_update` | Update an existing test plan. | `id`*, `name`, `description`, `stateName`, `stateId` |
| `test_plan_get` | Get test plan details. | `id`* |
| `test_plan_delete` | Delete a test plan. | `id`* |
| `test_case_search` | Search for test cases. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` (default: 10) |
| `test_case_create" | Create a new test case. | `name`*, `projectId`*, `description`, `testPlanId` |
| `test_case_update` | Update an existing test case. | `id`*, `name`, `description`, `stateName` |
| `test_case_get` | Get test case details. | `id`* |
| `test_case_delete` | Delete a test case. | `id`* |
| `test_step_create` | Add a step to a test case. | `testCaseId`*, `description`*, `expectedResult`, `runOrder` |
| `test_step_delete` | Delete a test step. | `id`* |

## 👥 Teams & 🔄 Sprints
| Tool | Description | Parameters |
| --- | --- | --- |
| `team_search` | Search for teams. | `nameQuery`, `take` (default: 10) |
| `team_get` | Get team details. | `id`* |
| `team_iteration_search` | Search for team iterations (sprints). | `nameQuery`, `teamId`, `teamName`, `startDate`, `endDate`, `take` (default: 10) |
| `team_iteration_get` | Get sprint details. | `id`* |

## 📁 Projects
| Tool | Description | Parameters |
| --- | --- | --- |
| `project_search` | Search for projects. | `nameQuery`, `startDate`, `endDate`, `take` (default: 10) |

## 👤 Users
| Tool | Description | Parameters |
| --- | --- | --- |
| `user_search` | Search for active users. | `query`*, `take` (default: 10) |

## 🔗 Relations
| Tool | Description | Parameters |
| --- | --- | --- |
| `relation_search` | Find relations linked to an entity. | `entityId`* |
| `relation_link` | Link two entities together. | `inboundId`*, `outboundId`*, `typeName` |

## 💬 Comments
| Tool | Description | Parameters |
| --- | --- | --- |
| `comment_add` | Add a comment to an entity (User Story, Task, Bug, Test Case, etc.). | `entityId`*, `text`* |

`*` denotes a required parameter.

---

### 📝 Description Format Support
When creating or updating entities (User Stories, bugs, Features, etc.), the `description` and `expectedResult` fields support both **Markdown** and **HTML**.

- **Markdown**: Standard Markdown syntax is automatically converted to HTML before being sent to Targetprocess.
- **HTML**: If the content starts and ends with HTML tags (e.g., `<div>...</div>`), it will be treated as raw HTML and passed through without conversion.

#### Examples
- Markdown: `**Bold text** and [links](http://example.com)` -> `<strong>Bold text</strong> and <a href="...">links</a>`
- HTML: `<p>Simple paragraph</p>` -> passed as is.

#### Mermaid Diagrams
To embed a Mermaid diagram in a description, base64-encode the Mermaid definition and use an image tag:
`<img src="https://mermaid.ink/img/<base64_string>" />`
