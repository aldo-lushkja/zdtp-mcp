# Native Binaries & Java 25 Cleanup Design

## Overview

Add native platform binaries (GraalVM + jpackage) and refactor codebase using Java 25 features to remove boilerplate.

## Native Binaries

### Approach

**GraalVM native-image:**

- Creates standalone binary (~30MB)
- Faster startup than JVM
- No JVM required at runtime

**jpackage:**

- Creates platform-specific installers
- macOS: .dmg / .app
- Linux: .deb, .rpm
- Windows: .exe, .msi
- Bunds JRE inside

**Recommendation:** Use both
- GraalVM for simple standalone binary
- jpackage for full installers

### Implementation

```bash
# GraalVM build
./gradlew nativeBuild

# jpackage build
./gradlew jpackage
```

### CI Integration

Add to `deploy.yml`:
- Build native binary on tags/releases
- Upload as release assets

## Shell Script Launcher

### Simple shell script (`zdtp`)

```bash
#!/bin/bash
JAR=$(dirname $0)/zdtp-mcp-*.all.jar
java -jar $JAR "$@"
```

- Auto-discovers versioned JAR
- Passes all args to JAR

## Java 25 Cleanup

### 1. Simplify ZdtpMcpApplication

**Current:** 228 lines of repetitive wiring

**After:** ~50 lines using factory pattern

```java
// Before
var epicConverter = new EpicConverter();
var epicMcpTools = new EpicMcpTools(
    new EpicSearchService(engine, epicConverter),
    new EpicCreateService(engine, epicConverter),
    ...
);

// After (registry pattern)
new DomainRegistry(engine)
    .register("epic", EpicConverter::new, EpicMcpTools::new)
    .register("feature", FeatureConverter::new, FeatureMcpTools::new)
    ...
    .registerAll(server, schema);
```

### 2. Consolidate CRUD Services

Each entity has:

- SearchService (list with filters)
- CreateService (POST new)
- UpdateService (POST update)
- GetByIdService (GET by ID)
- DeleteService (DELETE)

**Refactor:** Base generic service

```java
public record SearchCriteria(...) {}
public record CrudCriteria(...) {}

public class EntityService<E> {
    public List<E> search(SearchCriteria c) { ... }
    public E getById(Integer id) { ... }
    public E create(CrudCriteria c) { ... }
    public E update(Integer id, CrudCriteria c) { ... }
    public void delete(Integer id) { ... }
}
```

Each domain specifies:
- Entity record type
- DTO type  
- Converter
- Search criteria fields

### 3. Converter Utilities

**Current:**

```java
Optional.ofNullable(story.project()).map(Project::name).orElse(null)
```

**After:** Helper in record accessor

```java
// Static helper
String nullOr(Supplier<T> getter) {
    return getter.get() != null ? getter.get() : null;
}

// Or use records with map
project != null ? Optional.of(project.name()) : Optional.empty()
```

Or use `ObjectUtils.nullSafe()`:

```java
project?.name()  // Java 25 preview: relaxed chaining
```

## Testing Strategy

- Preserve existing unit tests
- Add integration test with GraalVM binary
- Test shell script on each platform

## Risks & Mitigations

| Risk | Mitigation |
|------|------------|
| GraalVM reflection issues | Add reflection config |
| jpackage platform-specific | Build on CI with matrix |
| Breaking converter changes | Keep DTO contract |

## Phases

1. **Phase 1:** Native binaries (GraalVM + jpackage task)
2. **Phase 2:** Shell script launcher
3. **Phase 3:** Clean ZdtpMcpApplication (registry)
4. **Phase 4:** Consolidate services
5. **Phase 5:** Converter utilities

## Notes

- Keep backward compatibility
- v2 API exploration is separate task
- Existing tests must pass throughout