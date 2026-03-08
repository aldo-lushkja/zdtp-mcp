# 🤖 CI/CD Pipeline & Git Flow

This project uses a automated CI/CD pipeline powered by GitHub Actions, designed to support the **Git Flow** branching strategy.

## 🌊 Branching Strategy (Git Flow)

We follow a standard Git Flow approach:

- **`main`**: Production-ready code. Every push/merge here creates a formal release.
- **`develop`**: Integration branch for features. Reflects the latest delivered development changes.
- **`feature/*`**: Work on specific features.
- **`release/*`**: Preparation for a new production release.
- **`hotfix/*`**: Quick fixes for production issues.

---

## 🛠️ Pipeline Stages

The pipeline is split into parallel jobs for maximum efficiency.

### 1. Continuous Integration (CI)
- **Trigger**: Every push to any branch.
- **Actions**: 
  - Runs unit tests.
  - Builds the shadow (fat) JAR.
  - Uploads the build artifact for subsequent jobs.

### 2. Staging / Development Registry
- **Trigger**: Pushes to `develop` or `release/*` branches.
- **Docker Tags**: 
  - `develop` branch -> `ghcr.io/...:develop`
  - `release/*` branch -> `ghcr.io/...:release`
- **Purpose**: Allows testing the latest changes in a containerized environment before merging to production.

### 3. Production Release
- **Trigger**: Pushes to `main` or creation of tags starting with `v*`.
- **Artifacts**:
  - **GitHub Release**: Automatically created with the standalone JAR attached.
  - **Docker Production Tags**:
    - Full version (e.g., `1.0.4`)
    - `latest`
- **Purpose**: Delivers the final product to end-users.

---

## 🚀 How to Release

1.  Merge your `release/*` or `develop` branch into `main`.
2.  (Optional but recommended) Create a git tag: `git tag -a v1.0.5 -m "Release version 1.0.5" && git push origin v1.0.5`.
3.  The pipeline will automatically:
    - Create the GitHub Release.
    - Build and push the production Docker image.
