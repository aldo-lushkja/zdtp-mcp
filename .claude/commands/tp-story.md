Create or update a Targetprocess user story following the validated WHY/WHAT/Sequence Diagram structure.

## Usage

`/tp-story` — guided creation of a new user story
`/tp-story update <id>` — append or replace the description of an existing story

---

## Step-by-step instructions

### 1. Gather inputs

Ask the user (or infer from context) for:
- **Story name** — concise, imperative title (e.g. `POST /v1/partner_code_batches — presigned S3 URL generation`)
- **WHY** — one paragraph explaining the business/operational justification
- **WHAT** — bullet-point implementation details (classes, endpoints, DB operations, feature flags, cron expressions, error cases, etc.)

> **Persistence stories** (any story whose WHAT includes a Flyway migration + entity + MyBatis mapper + DAO) **must also list the following deliverables** in the WHAT section:
> - `*Bean` record + `*BeanBuilder` — service-layer DTO
> - `*EntityToBeanConverter` + `*BeanToEntityConverter` — `@Component`, implements `Converter<>`
> - `*PersistenceService` — wraps DAO: `create`, `updateSkipNull`, `loadById`, `loadAll` (via `BatchLoadService`), plus typed query methods matching each DAO filter
> - `*Exception` hierarchy — `*NotPresentException`, `*VersionMismatchException` extending a base `*Exception`
> - `*PersistenceServiceTest` — Spring integration test covering CRUD, optimistic locking, not-present/version-mismatch exceptions, paginated `loadAll`, and each filter method
- **Effort** — story points (number)
- **Project** — use `mcp__zdtp-mcp__project_search` to resolve project name → ID
- **Sprint** (optional) — use `mcp__zdtp-mcp__team_iteration_search` to resolve sprint name → ID
- **Feature** (optional) — use `mcp__zdtp-mcp__feature_search` to resolve feature name → ID
- **Mermaid sequence diagram** (optional but strongly recommended) — raw `sequenceDiagram` block

### 2. Encode the Mermaid diagram (if provided)

Run the following Python snippet via Bash, substituting the actual diagram content:

```python
import zlib, base64, json, sys

diagram = """<PASTE_DIAGRAM_HERE>"""

payload = json.dumps({"code": diagram, "mermaid": {"theme": "default"}})
compressed = zlib.compress(payload.encode("utf-8"), level=9)
encoded = base64.urlsafe_b64encode(compressed).decode()
print("https://mermaid.ink/img/pako:" + encoded)
```

This produces a URL like `https://mermaid.ink/img/pako:eNp...` that renders the diagram as a PNG image — **visible inline in Targetprocess** without any plugin.

### 3. Build the HTML description

Use this exact structure:

```html
<h2>WHY</h2>
<p>{one paragraph justification}</p>

<h2>WHAT</h2>
<p><code>{MainClass}</code> + <code>{SecondaryClass}</code></p>
<ul>
  <li>...</li>
  <li>...</li>
</ul>

<h2>Sequence Diagram</h2>
<img src="{mermaid_ink_url}" alt="Sequence diagram" style="max-width:100%;" />
<p><small>Source: <a href="{mermaid_ink_url}">mermaid.ink</a></small></p>
```

Rules:
- Use `&#8212;` for em-dash, `&#8594;` for `→`, `&lt;` for `<`, `&gt;` for `>`, `&amp;` for `&`
- Use `<code>` for class names, table names, config keys, endpoints, cron expressions
- Use `<pre><code>` blocks for request/response payloads and threshold config examples
- If no Mermaid diagram is provided, omit the Sequence Diagram section entirely
- If a Mermaid diagram is provided, embed it as an `<img>` tag (renders inline in TP) AND include a `<pre><code class="language-mermaid">` block below it (for copy-paste fallback)

### 4. Create or update the story

**Create:**
```
mcp__zdtp-mcp__user_story_create(
  name: "...",
  description: "<html>...",
  projectId: <resolved>,
  effort: <points>,
  teamIterationId: <resolved or omit>,
  featureId: <resolved or omit>
)
```

**Update (existing story):**
```
mcp__zdtp-mcp__user_story_update(
  id: <story_id>,
  description: "<html>..."
)
```

### 5. Confirm

After creation/update, print a one-line summary:
```
✓ [<id>] <name> — <project>, <sprint or "no sprint">, <points> pts
```
And paste the mermaid.ink URL so the user can visually verify the diagram before opening Targetprocess.

---

## Mermaid diagram tips

- Always name participants with a short alias: `participant API as Plus API`
- Use `alt`/`else` blocks for error paths (4xx responses, feature-flag bypass, partial results)
- Use `loop` for batch-processing iterations
- Use `Note over X: text` for inline state annotations
- Keep participant count ≤ 6 for readability in TP's narrow description pane
- **Never use `{` or `}` in message labels** — curly braces break Mermaid parsing (causes 400 from mermaid.ink). Write `status=READY` not `{ status: READY }`
- **Avoid deeply nested blocks** — `loop` inside `alt/else` with 7+ participants hits complexity limits. Simplify by using shorter participant aliases and condensed message text
- Always verify the mermaid.ink URL returns HTTP 200 with `curl -s -o /dev/null -w "%{http_code}" <url>` before embedding in the description

---

## Example — POST presigned URL story

**Input Mermaid:**
```
sequenceDiagram
    participant Operator
    participant API as Plus API
    participant S3Svc as S3PreSignedUrlService
    participant S3

    Operator->>API: POST /v1/partner_code_batches?partner_id={partner_id}
    API->>API: Generate batch UUID (in-memory only)
    API->>S3Svc: generateWriteUrl(PARTNER_CODE_BATCH, partner_id, uuid)
    S3Svc->>S3: Request presigned PUT URL (expires 10h)
    S3-->>S3Svc: presigned URL
    S3Svc-->>API: presigned URL + s3_key
    API-->>Operator: 200 OK { id, presigned_url, s3_key, expiration_date }
```

**Encoding command:**
```bash
python3 -c "
import zlib, base64, json
diagram = open('/dev/stdin').read()
payload = json.dumps({'code': diagram, 'mermaid': {'theme': 'default'}})
compressed = zlib.compress(payload.encode('utf-8'), level=9)
print('https://mermaid.ink/img/pako:' + base64.urlsafe_b64encode(compressed).decode())
" <<'EOF'
sequenceDiagram
    participant Operator
    ...
EOF
```
