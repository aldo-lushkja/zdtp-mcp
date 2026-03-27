# 🎯 Targetprocess MCP Server — Demo Output

> All queries run against a sample Targetprocess instance using synthetic data.

---

## 👥 Team & Sprint

---

### Prompt
> *"Show me all team iterations for [team name] in the last 3 months."*

**Query:** `searchTeamIterations` — team: Alpha Team, 2025-12-07 → 2026-03-07

| ID | Name | Start | End |
|----|------|-------|-----|
| 10016 | Alpha Team 2026-2-23 | 2026-02-22 | 2026-03-08 |
| 10012 | Alpha Team 2026-2-9 | 2026-02-08 | 2026-02-22 |
| 10008 | Alpha Team 2026-1-26 | 2026-01-25 | 2026-02-08 |
| 10004 | Alpha Team 2026-1-12 | 2026-01-11 | 2026-01-25 |
| 10002 | Alpha Team 2025-12-29 | 2025-12-28 | 2026-01-11 |
| 10001 | Alpha Team 2025-12-15 | 2025-12-14 | 2025-12-28 |

---

### Prompt
> *"What is the current team iteration? List all user stories planned in it."*

**Query:** `searchTeamIterations` + `searchUserStories` — sprintId: 10016

**Current sprint:** Alpha Team 2026-2-23 (2026-02-22 → 2026-03-08)

| ID | Name | Project | State | Pts |
|----|------|---------|-------|-----|
| 2101 | Implement OAuth2 login flow | [BE] Payments API | In Testing | 8 |
| 2098 | Add retry logic to payment processor | [BE] Payments API | Done | 3 |
| 2095 | Fix session timeout on mobile | [Mobile] Consumer App | Done | 1 |
| 2091 | Improve onboarding wizard UX | [FE] Web Portal | In Staging | 5 |
| 2087 | Add export to CSV for reports | [FE] Web Portal | Done | 2 |
| 2084 | Refactor notification service | [BE] Notification Service | Done | 3 |
| 2081 | Fix race condition in payment queue | [BE] Payments API | In Testing | 2 |
| 2078 | Add dark mode support | [Mobile] Consumer App | Open | 5 |
| 2075 | Write integration tests for auth module | [BE] Payments API | Done | 3 |
| 2072 | Update API documentation | [BE] Payments API | Done | 1 |

**10 stories · 33 pts** — Done: 5, In Testing: 2, In Staging: 1, Open: 1

---

### Prompt
> *"Find the latest sprint and show all releases associated with it."*

**Query:** `searchTeamIterations` + `searchReleases` — teamIterationId: 10016

| ID | Release Name | Project | State |
|----|-------------|---------|-------|
| 301 | v2.4.0 | [BE] Payments API | In Progress |
| 302 | v1.9.0 | [FE] Web Portal | Planned |
| 303 | v3.1.0 | [Mobile] Consumer App | In Progress |

---

## 🐛 Bugs & Issues

---

### Prompt
> *"List all open bugs assigned to me."*

**Query:** `searchBugs` — assignee: john.doe, state: Open

| ID | Name | Project | Priority | State |
|----|------|---------|----------|-------|
| 5042 | Payment confirmation email not sent | [BE] Payments API | High | Open |
| 5038 | Crash on iOS 17 when opening profile | [Mobile] Consumer App | Critical | Open |
| 5031 | Incorrect total on invoice PDF | [FE] Web Portal | Medium | Open |

---

### Prompt
> *"Show me all critical bugs created this week."*

**Query:** `searchBugs` — priority: Critical, createdAfter: 2026-03-01

| ID | Name | Project | Assignee | State |
|----|------|---------|----------|-------|
| 5038 | Crash on iOS 17 when opening profile | [Mobile] Consumer App | jane.smith | Open |
| 5041 | Database timeout under high load | [BE] Payments API | john.doe | In Progress |

---

## 📋 User Stories

---

### Prompt
> *"Find all user stories in 'In Progress' state for project Payments API."*

**Query:** `searchUserStories` — project: Payments API, state: In Progress

| ID | Name | Assignee | Points | Sprint |
|----|------|----------|--------|--------|
| 2081 | Fix race condition in payment queue | john.doe | 2 | Alpha Team 2026-2-23 |
| 2065 | Add webhook support for payment events | jane.smith | 5 | Alpha Team 2026-2-9 |

---

### Prompt
> *"Create a new user story for adding rate limiting to the API."*

**Tool:** `user_story_create`

```json
{
  "name": "Add rate limiting to public API endpoints",
  "description": "Implement token bucket rate limiting for all public API endpoints to prevent abuse.",
  "projectId": 101,
  "teamId": 201,
  "effort": 5
}
```

**Result:** Created UserStory #2110 — "Add rate limiting to public API endpoints"

---

## ✅ Test Cases

---

### Prompt
> *"List all test cases for the login feature."*

**Query:** `searchTestCases` — name contains "login"

| ID | Name | Project | State |
|----|------|---------|-------|
| 8021 | Login with valid credentials | [BE] Payments API | Passed |
| 8022 | Login with invalid password | [BE] Payments API | Passed |
| 8023 | Login with expired token | [BE] Payments API | Failed |
| 8024 | Login rate limiting | [BE] Payments API | Not Run |

---

## 🔗 Relations

---

### Prompt
> *"Find all user stories that block bug #5042."*

**Query:** `relation_search` — entity: Bug#5042, relationType: blocks

| Relation | Entity Type | ID | Name |
|----------|------------|-----|------|
| blocks | UserStory | 2081 | Fix race condition in payment queue |

---

## 👤 Users

---

### Prompt
> *"Search for users in the Alpha Team."*

**Query:** `user_search` — team: Alpha Team

| ID | Name | Email | Role |
|----|------|-------|------|
| 401 | John Doe | john.doe@example.com | Developer |
| 402 | Jane Smith | jane.smith@example.com | QA Engineer |
| 403 | Alice Johnson | alice.johnson@example.com | Team Lead |
| 404 | Bob Williams | bob.williams@example.com | Developer |
