# 🎯 Lightweight Targetprocess MCP Server

A high-performance Java application implementing the [Model Context Protocol (MCP)](https://modelcontextprotocol.io) to expose Targetprocess data to AI assistants. Built with a **Zero-Framework** policy for maximum speed and portability.

---

## 🚀 Quick Start

### ♊ Gemini CLI

```bash
gemini mcp add zdtp docker run -i --rm \
  -e TP_URL="https://youraccount.tpondemand.com" \
  -e TP_TOKEN="your_token" \
  ghcr.io/owner/zdtp-mcp:latest
```

### 🔌 Claude Code CLI

```bash
claude mcp add zdtp -- docker run -i --rm \
  -e TP_URL="https://youraccount.tpondemand.com" \
  -e TP_TOKEN="your_token" \
  ghcr.io/owner/zdtp-mcp:latest
```

---

## ⚙️ Configuration

Set the following environment variables:

| Variable | Description |
| --- | --- |
| `TP_URL` | Your Targetprocess instance URL (e.g., `https://youraccount.tpondemand.com`) |
| `TP_TOKEN` | Your API token (Profile → Settings → API Access Tokens) |

---

## 🛠️ Available Tools

The server exposes a comprehensive set of tools for interacting with Targetprocess entities, including **User Stories, Epics, Features, Releases, Sprints, Test Plans/Cases, Teams, and Projects**.

For a complete list of tools, their descriptions, and parameters, please refer to the:
👉 **[Available Tools Reference](docs/TOOLS.md)**

---

## 💬 Example Prompts
- *"Find all open user stories assigned to me created this week."*
- *"List all releases in the current sprint for Team Alpha."*
- *"Create a new test case for 'Login validation' in Project X."*

---

## 📚 Reference Documentation
- [🛠️ Available Tools Reference](docs/TOOLS.md)
- [🏗️ Architecture](docs/ARCHITECTURE.md)
- [🗂️ Data Model](docs/DATA_MODEL.md)
- [🛠️ Development & Build](docs/DEVELOPMENT.md)

---

## 🤝 Community & Legal
- [📄 License](LICENSE)
- [🤝 Contributing](CONTRIBUTING.md)
- [⚖️ Code of Conduct](CODE_OF_CONDUCT.md)
