# AGENTS.md - Agentic Coding Guidelines

## Project Overview

This is a **Java/Gradle project** using Vert.x and Netty with SSL/TLS support (netty-tcnative OpenSSL).
- **Language**: Java 25
- **Build Tool**: Gradle 9.3.1
- **Framework**: Vert.x Web 4.5.11, Netty 4.1.115
- **Testing**: JUnit 5 (Jupiter) 5.11.0
- **Main Class**: `com.example.Main`

---

## Build & Test Commands

### Core Commands

```bash
# Build project (compiles + runs tests)
./gradlew build

# Clean and rebuild
./gradlew clean build

# Run application
./gradlew run

# Run tests only
./gradlew test

# Run single test class
./gradlew test --tests "com.example.MyTestClass"

# Run single test method
./gradlew test --tests "com.example.MyTestClass.myTestMethod"

# Run tests matching pattern
./gradlew test --tests "*IntegrationTest"

# Skip tests during build
./gradlew build -x test

# Run application with custom SSL certs
./gradlew run -Dcert.path=cert.pem -Dkey.path=key.pem

# View all available tasks
./gradlew tasks

# View dependency tree
./gradlew dependencies
```

---

## Code Style Guidelines

### General Conventions

- **Indentation**: 4 spaces (no tabs)
- **Line Length**: Target < 120 characters
- **Encoding**: UTF-8
- **Java Version**: 25 (source and target compatibility)

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `Main`, `SslConfig` |
| Methods | camelCase | `getSslContext()`, `main()` |
| Variables | camelCase | `httpsPort`, `keyPath` |
| Constants | UPPER_SNAKE_CASE | `HTTPS_PORT = 8443` |
| Packages | lowercase | `com.example` |

### Code Patterns

**Use modern Java patterns:**
- Try-with-resources for auto-closeable resources
- Var for local variable type inference (when type is obvious)
- Method references where appropriate
- Functional interfaces (Consumer, Supplier, Function) with lambdas

**Example - Try-with-resources:**
```java
try (FileInputStream fis = new FileInputStream(keyStorePath)) {
    keyStore.load(fis, keyStorePassword.toCharArray());
}
```

**Example - Builder pattern (Vert.x):**
```java
HttpServerOptions options = new HttpServerOptions()
    .setSsl(true)
    .setPemKeyCertOptions(new PemKeyCertOptions()
        .setKeyPath(keyPath)
        .setCertPath(certPath))
    .setPort(HTTPS_PORT);
```

### Error Handling

- Use try-catch for recoverable errors; let unrecoverable errors propagate
- Always log or handle exceptions meaningfully - never silently swallow
- Prefer specific exception types over generic `Exception`
- Use `.onFailure()` handlers for Vert.x async results

```java
// Good - specific handling
server.listen()
    .onSuccess(s -> System.out.println("Started on port " + s.actualPort()))
    .onFailure(err -> {
        System.err.println("Failed: " + err.getMessage());
        err.printStackTrace();
    });
```

### Javadoc

**Required** for:
- All public classes
- All public/protected methods
- Complex algorithm explanations

**Format:**
```java
/**
 * Creates SSL context from a PKCS12 or JKS key store.
 *
 * @param keyStorePath     Path to the key store file
 * @param keyStorePassword Password for the key store
 * @param keyPassword      Password for the private key
 * @return SslConfig instance
 * @throws Exception if key store cannot be loaded
 */
```

### Type Safety

- **NEVER** suppress type warnings with `@SuppressWarnings`
- **NEVER** use raw types (use `<String>` generics)
- Prefer `List<T>`, `Map<K,V>` over array collections
- Use `Optional<T>` for nullable returns where appropriate

### Testing Guidelines

- Test class naming: `<ClassName>Test` or `<ClassName>IT` for integration tests
- Test method naming: `should<ExpectedBehavior>` or `test<Description>`
- Use JUnit 5 features: `@DisplayName`, `@Nested`, `@ParameterizedTest`
- One assertion per test when possible for clear failure messages
- Test location: `src/test/java/com/example/`

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class SslConfigTest {

    @Test
    @DisplayName("should load keystore successfully")
    void shouldLoadKeyStore() {
        // test implementation
    }
}
```

---

## Project Structure

```
mini-blob-store/
├── src/
│   ├── main/
│   │   └── java/com/example/
│   │       ├── Main.java          # Application entry point
│   │       └── SslConfig.java     # SSL configuration
│   └── test/
│       └── java/com/example/       # Test files go here
├── build.gradle                    # Build configuration
├── gradlew                         # Gradle wrapper
└── AGENTS.md                       # This file
```

---

## Running the Application

The application starts an HTTPS server on port 8443 by default.

```bash
# Build and run
./gradlew run

# Or run with custom port
# (edit Main.java HTTPS_PORT constant or pass via code modification)
```

Endpoints:
- `GET /` - Returns JSON greeting
- `GET /health` - Health check with TLS status

---

## Important Notes

**No CI yet**: Tests currently have no test source files - add tests to `src/test/java/com/example/`.
