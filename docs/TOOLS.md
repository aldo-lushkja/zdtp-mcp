# 🛠️ Available Tools Reference

This document provides a detailed reference for all tools exposed by the Targetprocess MCP server.

## 📖 User Stories
| Tool | Description | Parameters |
| --- | --- | --- |
| `user_story_search` | Search for user stories. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `releaseId`, `sprintId`, `take` (default: 10) |
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
| `epic_delete` | Delete an epic. | `id`* |

## ✨ Features
| Tool | Description | Parameters |
| --- | --- | --- |
| `feature_search` | Search for features. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` (default: 10), `sprintId` |
| `feature_create` | Create a new feature. | `name`*, `projectId`*, `description`, `effort` |
| `feature_update` | Update an existing feature. | `id`*, `name`, `description`, `stateName`, `effort` |
| `feature_get` | Get feature details. | `id`* |
| `feature_delete` | Delete a feature. | `id`* |

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
| `release_delete` | Delete a release. | `id`* |

## 📬 Requests
| Tool | Description | Parameters |
| --- | --- | --- |
| `request_search` | Search for requests. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` (default: 10) |
| `request_create` | Create a new request. | `name`*, `projectId`*, `description`, `effort` |
| `request_update` | Update an existing request. | `id`*, `name`, `description`, `stateName`, `effort` |
| `request_get` | Get request details. | `id`* |
| `request_delete` | Delete a request. | `id`* |

## 🧪 Quality Assurance (Test Plans, Cases & Execution Runs)
| Tool | Description | Parameters |
| --- | --- | --- |
| `test_plan_search` | Search for test plans. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` (default: 10) |
| `test_plan_create` | Create a new test plan. | `name`*, `projectId`*, `description` |
| `test_plan_update` | Update an existing test plan. | `id`*, `name`, `description`, `stateName`, `stateId` |
| `test_plan_get` | Get test plan details. | `id`* |
| `test_plan_delete` | Delete a test plan. | `id`* |
| `test_case_search` | Search for test cases. | `nameQuery`, `projectName`, `ownerLogin`, `startDate`, `endDate`, `take` (default: 10) |
| `test_case_create` | Create a new test case. | `name`*, `projectId`*, `description`, `testPlanId` |
| `test_case_update` | Update an existing test case. | `id`*, `name`, `description`, `stateName` |
| `test_case_get` | Get test case details. | `id`* |
| `test_case_delete` | Delete a test case. | `id`* |
| `test_step_create` | Add a step to a test case. | `testCaseId`*, `description`*, `expectedResult`, `runOrder` |
| `test_step_delete` | Delete a test step. | `id`* |
| `test_run_create` | Create a new test run for a test plan. | `name`*, `projectId`*, `testPlanId`*, `description` |
| `test_run_search` | Search for test runs. | `nameQuery`, `projectId`, `testPlanId`, `take` (default: 10) |
| `test_case_run_update` | Update status or comment of a test case run. | `id`*, `status`*, `comment` |

## 👥 Teams & 🔄 Sprints / Iterations
| Tool | Description | Parameters |
| --- | --- | --- |
| `team_search` | Search for teams. | `nameQuery`, `take` (default: 10) |
| `team_get` | Get team details. | `id`* |
| `team_iteration_search` | Search for team iterations (team sprints). | `nameQuery`, `teamId`, `teamName`, `startDate`, `endDate`, `take` (default: 10) |
| `team_iteration_get` | Get team sprint details. | `id`* |
| `iteration_search` | Search for project iterations (global sprints). | `nameQuery`, `projectId`, `startDate`, `endDate`, `take` (default: 10) |
| `iteration_get` | Get project iteration details. | `id`* |

## 📁 Projects & Workflows
| Tool | Description | Parameters |
| --- | --- | --- |
| `project_search` | Search for projects. | `nameQuery`, `startDate`, `endDate`, `take` (default: 10) |
| `workflow_state_list` | List available workflow states for an entity type (e.g. UserStory, Task, Bug). | `entityTypeName`*, `processId` |

## 🏷️ Tagging
| Tool | Description | Parameters |
| --- | --- | --- |
| `tag_add` | Add a tag to an entity (User Story, Task, Bug, Feature, Epic, Request). | `entityId`*, `tag`* |
| `tag_remove` | Remove a tag from an entity. | `entityId`*, `tag`* |

## 🧩 Custom Fields
| Tool | Description | Parameters |
| --- | --- | --- |
| `custom_field_list` | List custom field definitions for an entity type or process. | `entityTypeName`, `processId`, `take` (default: 10) |
| `custom_field_update` | Update a custom field value on an entity. | `entityId`*, `fieldName`*, `fieldValue`* |

## 👥 Role Assignments
| Tool | Description | Parameters |
| --- | --- | --- |
| `assignment_add` | Assign a user to an entity with an optional role. | `entityId`*, `userId`*, `roleId` |
| `assignment_remove` | Remove an assignment by ID. | `id`* |

## 📎 Attachments
| Tool | Description | Parameters |
| --- | --- | --- |
| `attachment_search` | List attachments linked to an entity. | `entityId`*, `take` (default: 10) |

## ⏱️ Time Tracking
| Tool | Description | Parameters |
| --- | --- | --- |
| `time_log` | Log time spent/remaining on an assignable entity. | `entityId`*, `spent`*, `remain`, `description`, `date`, `userId` |

## ⚠️ Impediments
| Tool | Description | Parameters |
| --- | --- | --- |
| `impediment_create` | Create a new impediment on an entity. | `entityId`*, `name`*, `description`, `assignedToId`, `stateName` |
| `impediment_search` | Search for impediments. | `nameQuery`, `entityId`, `take` (default: 10) |

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

## 🏷️ Tags
| Tool | Description | Parameters |
| --- | --- | --- |
| `tag_add` | Add a tag to an assignable entity. | `entityId`*, `tagName`* |
| `tag_remove` | Remove a tag from an assignable entity. | `entityId`*, `tagName`* |

## ⚙️ Custom Fields
| Tool | Description | Parameters |
| --- | --- | --- |
| `custom_field_list` | List custom field definitions for a process/project. | `processId`, `entityTypeName`, `take` (default: 50) |
| `custom_field_update` | Update a custom field value on an entity. | `entityId`*, `fieldName`*, `value`* |

## 👥 Role Assignments
| Tool | Description | Parameters |
| --- | --- | --- |
| `assignment_add` | Assign a user to an assignable entity under a role. | `entityId`*, `userId`*, `roleName` |
| `assignment_remove` | Remove a user assignment from an entity. | `entityId`*, `userId`* |

## ⚙️ System Meta
| Tool | Description | Parameters |
| --- | --- | --- |
| `server_changelog` | Returns the full zdtp-mcp changelog. | *None* |
| `server_health` | Returns runtime health status, JVM memory metrics (used/total/max MB), active threads, and uptime. | *None* |

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
