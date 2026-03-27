<div align="center">

# zdtp-mcp

**A lightweight, zero-framework MCP server for [Targetprocess](https://www.targetprocess.com)**

[![Version](https://img.shields.io/github/v/tag/aldo-lushkja/zdtp-mcp?label=version&color=blue)](https://github.com/aldo-lushkja/zdtp-mcp/releases)
[![License](https://img.shields.io/github/license/aldo-lushkja/zdtp-mcp)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Platforms](https://img.shields.io/badge/docker-amd64%20%7C%20arm64-blue)](https://ghcr.io/aldo-lushkja/zdtp-mcp)

Manage your Targetprocess projects directly from any MCP-compatible AI assistant.
**52 tools** across **15 domains** — one Docker command to get started.

[Quick Start](#-quick-start) · [Tools Reference](docs/TOOLS.md) · [Docker Guide](DOCKER.md) · [Architecture](docs/ARCHITECTURE.md)

</div>

---

## Why zdtp-mcp?

| | |
|---|---|
| **Zero framework** | Pure Java 21 — no Spring, no Quarkus. Minimal footprint, instant startup |
| **52 tools** | Full CRUD across User Stories, Tasks, Bugs, Epics, Features, Releases, Test Plans, and more |
| **Multi-platform** | Single Docker image for both `linux/amd64` and `linux/arm64` (Apple Silicon native) |
| **Any AI client** | Works with Claude Code, Gemini CLI, Claude Desktop, and any MCP-compatible assistant |

---

## 🚀 Quick Start

### Claude Code CLI

```bash
claude mcp add zdtp -- docker run -i --rm \
  -e TP_URL="https://youraccount.tpondemand.com" \
  -e TP_TOKEN="your_token" \
  ghcr.io/aldo-lushkja/zdtp-mcp:latest
```

### Gemini CLI

```bash
gemini mcp add zdtp docker run -i --rm \
  -e TP_URL="https://youraccount.tpondemand.com" \
  -e TP_TOKEN="your_token" \
  ghcr.io/aldo-lushkja/zdtp-mcp:latest
```

### Claude Desktop (`claude_desktop_config.json`)

```json
{
  "mcpServers": {
    "zdtp": {
      "command": "docker",
      "args": [
        "run", "-i", "--rm",
        "-e", "TP_URL=https://youraccount.tpondemand.com",
        "-e", "TP_TOKEN=your_token",
        "ghcr.io/aldo-lushkja/zdtp-mcp:latest"
      ]
    }
  }
}
```

### Environment Variables

| Variable | Description |
| --- | --- |
| `TP_URL` | Your Targetprocess instance URL (e.g., `https://youraccount.tpondemand.com`) |
| `TP_TOKEN` | Your API token — Profile → Settings → API Access Tokens |

---

## 🛠️ Available Tools (52)

| Domain | Tools |
|--------|-------|
| User Stories | search, create, update, get, delete |
| Tasks | search, create, update, get, delete |
| Bugs | search, create, update, get, delete |
| Epics | search, create, update, get, delete |
| Features | search, create, update, get, delete |
| Releases | search, create, update, get, delete |
| Requests | search, create, update, get, delete |
| Test Plans | search, create, update, get, delete |
| Test Cases | search, create, update, get, delete, add step, delete step |
| Teams | search, get |
| Sprints | search, get |
| Projects | search |
| Users | search |
| Relations | search, create, delete |
| Comments | add |

👉 Full parameter reference: **[docs/TOOLS.md](docs/TOOLS.md)**

---

## 💬 Example Prompts

```
"Find all open bugs assigned to me in Project Alpha"
"Create a user story for the login feature in sprint 42"
"Link US-123 as a blocker of US-456"
"Add a test case with steps for the payment flow"
"Show me all releases due this month"
```

---

## 📚 Documentation

- [🛠️ Tools Reference](docs/TOOLS.md)
- [🐳 Docker Guide](DOCKER.md)
- [🏗️ Architecture](docs/ARCHITECTURE.md)
- [🗂️ Data Model](docs/DATA_MODEL.md)
- [⚙️ Development & Build](docs/DEVELOPMENT.md)
- [🤖 CI/CD & Git Flow](docs/CICD.md)

---

## 🤝 Community & Legal

- [Contributing](CONTRIBUTING.md)
- [Code of Conduct](CODE_OF_CONDUCT.md)
- [License](LICENSE)
