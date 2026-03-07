# Contributing to Zero Dependency Targetprocess MCP Server

Thank you for your interest in contributing! We welcome all contributions that improve the project.

## 🚀 How to Contribute

1.  **Fork the repository** and create your branch from `main`.
2.  **Run the existing tests** to ensure they pass: `./gradlew test`.
3.  **Make your changes**. If you're adding a feature, please include tests.
4.  **Verify your changes** by running tests again and building the fat JAR: `./gradlew shadowJar`.
5.  **Submit a Pull Request** with a clear description of what your change does.

## 🧪 Testing

We use JUnit 5 and Mockito. Please ensure that:
*   New services have unit tests.
*   Existing functionality is not broken.
*   The coverage remains high.

## 🎨 Coding Standards

*   Follow standard Java naming conventions.
*   Ensure zero external framework dependencies (standard Java only, except for core JSON/HTTP needs if absolutely necessary).
*   Keep the BCE (Boundary-Control-Entity) architectural pattern.

## 📝 Commit Messages

*   Use clear, descriptive commit messages.
*   Keep the first line under 50 characters if possible.

## 🤝 Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).
