# Docker Guide

The published image is a JVM-based multi-platform image (`linux/amd64` + `linux/arm64`) available on GitHub Container Registry.

## Using the Published Image

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

### Environment Variables

| Variable  | Description |
| --------- | ----------- |
| `TP_URL`  | Your Targetprocess instance URL (e.g., `https://youraccount.tpondemand.com`) |
| `TP_TOKEN`| Your API token (Profile → Settings → API Access Tokens) |

## Available Tags

| Tag | Description |
| --- | --- |
| `latest` | Latest stable release (mirrors `main`) |
| `1.x.y` | Specific version |
| `release` | Current release candidate (from `release/**` branches) |
| `develop` | Latest development build |

## Building Locally

```bash
# Build the JVM image
docker build --target jvm -t zdtp-mcp:local .

# Run it
docker run -i --rm \
  -e TP_URL="https://youraccount.tpondemand.com" \
  -e TP_TOKEN="your_token" \
  zdtp-mcp:local
```
