# 🎯 Zero Dependency Targetprocess MCP Server

A lightweight Java application implementing the [Model Context Protocol (MCP)](https://modelcontextprotocol.io) to expose Targetprocess data to AI assistants.

---

## 🚀 Quick Start

### ♊ Gemini CLI

```bash
gemini mcp add zdtp java -jar "/path/to/zdtp-mcp-1.0.0-all.jar" \
  -e TARGETPROCESS_BASE_URL="https://youraccount.tpondemand.com" \
  -e TARGETPROCESS_ACCESS_TOKEN="your_token"
```

### 🔌 Claude Code CLI

```bash
claude mcp add zdtp -- java -jar "/path/to/zdtp-mcp-1.0.0-all.jar"
```

---

## ⚙️ Configuration

Set the following environment variables:

| Variable | Description |
| --- | --- |
| `TARGETPROCESS_BASE_URL` | Your Targetprocess instance URL (e.g., `https://youraccount.tpondemand.com`) |
| `TARGETPROCESS_ACCESS_TOKEN` | Your API token (Profile → Settings → API Access Tokens) |

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
