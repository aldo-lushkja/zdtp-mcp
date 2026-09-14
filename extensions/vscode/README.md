# Targetprocess MCP — VS Code / Cursor Extension

Official VS Code / Cursor extension for **zdtp-mcp** (Targetprocess Model Context Protocol server).

## 🚀 Features
- Integrates Targetprocess project management (69 tools) directly with VS Code / Cursor AI assistants.
- Configurable URL, API token, and Docker/JAR execution modes.

## ⚙️ Configuration
In your VS Code / Cursor Settings (`ctrl+,` or `cmd+,`), search for **Targetprocess MCP**:

1. **`zdtp.tpUrl`**: `https://youraccount.tpondemand.com`
2. **`zdtp.tpToken`**: Your Targetprocess API Access Token
3. **`zdtp.useDocker`**: `true` (default) or `false`

## 📦 How to Build `.vsix` Package

```bash
cd extensions/vscode
npx @vscode/vsce package
```

This generates `zdtp-mcp-vscode-1.2.0.vsix` which can be installed in VS Code or Cursor via **Extensions -> Install from VSIX...**.
