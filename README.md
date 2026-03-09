# Mini Blob Store

A simplified HTTP blob store proxy built as a learning project to deeply understand **Vert.x**, **Netty**, and **netty-tcnative** (OpenSSL/BoringSSL).

## Project Goals

This project explores:

1. **Vert.x Web** — Async HTTP routing and request handling
2. **Netty** — Low-level byte streaming for efficient file I/O without loading entire files into memory
3. **netty-tcnative** — Native TLS/SSL offloading via OpenSSL/BoringSSL for performance benchmarking

The end goal is an HTTPS server that accepts `PUT` and `GET` requests for files, streaming bytes asynchronously to/from the local filesystem in chunks.

## Current Status

| Feature | Status |
|---------|--------|
| HTTPS server with TLS | ✅ Implemented |
| OpenSSL via netty-tcnative | ✅ Integrated |
| Basic endpoints (`/`, `/health`) | ✅ Implemented |
| OCI-native API (`/o`, `/o/:objectName`) | ✅ Implemented |
| S3-compatible API (`/:bucket`) | ✅ Implemented |
| File PUT handler (streaming upload) | 🚧 Planned |
| File GET handler (streaming download) | 🚧 Planned |
| Chunked async file I/O | 🚧 Planned |
| TLS benchmarking | 🚧 Planned |
|---------|--------|
| HTTPS server with TLS | ✅ Implemented |
| OpenSSL via netty-tcnative | ✅ Integrated |
| Basic endpoints (`/`, `/health`) | ✅ Implemented |
| File PUT handler (streaming upload) | 🚧 Planned |
| File GET handler (streaming download) | 🚧 Planned |
| Chunked async file I/O | 🚧 Planned |
| TLS benchmarking | 🚧 Planned |

### What's Built

A minimal HTTPS server demonstrating Vert.x + Netty + OpenSSL integration with OCI-native and S3-compatible APIs:

```
GET /                    → {"message": "Hello from Vert.x + Netty + TLS!"}
GET /health              → {"status": "ok", "tls": "enabled"}
GET /o?prefix=logs/      → List objects with prefix (OCI-native)
PUT /o/:objectName       → Upload object with Content-Length (OCI-native)
GET /:bucket?list-type=2 → List objects (S3-compatible ListObjectsV2)
```

A minimal HTTPS server demonstrating Vert.x + Netty + OpenSSL integration:

```
GET /       → {"message": "Hello from Vert.x + Netty + TLS!"}
GET /health → {"status": "ok", "tls": "enabled"}
```

## Tech Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| Java | 25 | Runtime |
| Gradle | 9.3.1 | Build tool |
| Vert.x Web | 4.5.11 | HTTP routing, async server |
| Netty | 4.1.115.Final | Low-level networking, byte streaming |
| netty-tcnative | 2.0.70.Final | Native OpenSSL/BoringSSL for TLS |
| JUnit 5 | 5.11.0 | Testing |

## Quick Start

### Prerequisites

- Java 25 (or 21+)
- Gradle 9.x (wrapper included)

### Generate Self-Signed Certificates (Development)

```bash
# Generate key and certificate
openssl req -x509 -newkey rsa:2048 -keyout key.pem -out cert.pem -days 365 -nodes -subj "/CN=localhost"

# Or use the existing cert.pem and key.pem in the project root
```

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew run

# With custom certificate paths
./gradlew run -Dcert.path=/path/to/cert.pem -Dkey.path=/path/to/key.pem
```

The server starts on **https://localhost:8443**

### Test Endpoints

```bash
# Basic health check
curl -k https://localhost:8443/health

# Expected: {"status": "ok", "tls": "enabled"}

# Root endpoint
curl -k https://localhost:8443/

# Expected: {"message": "Hello from Vert.x + Netty + TLS!"}

# OCI-native ListObjects
curl -k "https://localhost:8443/o?prefix=logs/"

# OCI-native PutObject
curl -k -X PUT "https://localhost:8443/o/test.txt" \
  -H "Content-Length: 13" \
  -d "Hello, World!"

# S3-compatible ListObjectsV2
curl -k "https://localhost:8443/mybucket?list-type=2&prefix=logs/"
```
# Basic health check
curl -k https://localhost:8443/health

# Expected: {"status": "ok", "tls": "enabled"}

# Root endpoint
curl -k https://localhost:8443/

# Expected: {"message": "Hello from Vert.x + Netty + TLS!"}
```

## Project Structure

```
mini-blob-store/
├── src/main/java/com/blob/
│   ├── Main.java                    # Application entry point, Vert.x server setup
│   ├── SslConfig.java               # SSL context utilities (PKCS12/JKS, mTLS support)
│   └── model/
│       ├── ObjectSummary.java       # Object metadata model
│       ├── ListObjectsResponse.java # OCI-native list objects response
│       └── s3/
│           ├── S3Object.java        # S3-compatible object model
│           ├── S3ListBucketResult.java  # S3 ListBucket response
│           └── CommonPrefix.java    # S3 common prefix for delimiters
├── build.gradle                     # Dependencies: Vert.x, Netty, netty-tcnative
├── cert.pem                         # Development self-signed certificate
├── key.pem                          # Development private key
├── AGENTS.md                        # Code style guidelines for AI assistants
└── README.md                        # This file


## Architecture

### Current Implementation

```
┌─────────────────────────────────────────────────────┐
│                     Client                          │
└─────────────────────┬───────────────────────────────┘
                      │ HTTPS
                      ▼
┌─────────────────────────────────────────────────────┐
│              Vert.x HttpServer                      │
│  ┌───────────────────────────────────────────────┐  │
│  │  Router                                        │  │
│  │  ├── GET /              → Health check        │  │
│  │  ├── GET /health        → TLS status          │  │
│  │  ├── GET /o             → ListObjects (OCI)   │  │
│  │  ├── PUT /o/:name       → PutObject (OCI)     │  │
│  │  └── GET /:bucket       → ListObjectsV2 (S3)  │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│           netty-tcnative (OpenSSL)                  │
│           Native TLS termination                    │
└─────────────────────────────────────────────────────┘
```

```
┌─────────────────────────────────────────────────────┐
│                     Client                          │
└─────────────────────┬───────────────────────────────┘
                      │ HTTPS
                      ▼
┌─────────────────────────────────────────────────────┐
│              Vert.x HttpServer                      │
│  ┌───────────────────────────────────────────────┐  │
│  │  Router (GET /, GET /health)                  │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│           netty-tcnative (OpenSSL)                  │
│           Native TLS termination                    │
└─────────────────────────────────────────────────────┘
```

### Planned Architecture (Blob Store)

```
┌─────────────────────────────────────────────────────┐
│                     Client                          │
└─────────────────────┬───────────────────────────────┘
                      │ HTTPS
                      ▼
┌─────────────────────────────────────────────────────┐
│              Vert.x HttpServer                      │
│  ┌───────────────────────────────────────────────┐  │
│  │  Router                                        │  │
│  │  ├── GET /              → Health check        │  │
│  │  ├── GET /health        → TLS status          │  │
│  │  ├── GET /o             → ListObjects (OCI)   │  │
│  │  ├── PUT /o/:name       → PutObject (OCI)     │  │
│  │  ├── GET /:bucket       → ListObjectsV2 (S3)  │  │
│  │  ├── PUT /:bucket/:key  → Stream to disk      │  │
│  │  ├── GET /:bucket/:key  → Stream from disk    │  │
│  │  └── DELETE /:bucket/:key → Remove file       │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│           Netty Async File I/O                      │
│           Chunked streaming (no full buffering)     │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│           Local Filesystem                          │
│           ./data/ directory                         │
└─────────────────────────────────────────────────────┘
```

```
┌─────────────────────────────────────────────────────┐
│                     Client                          │
└─────────────────────┬───────────────────────────────┘
                      │ HTTPS
                      ▼
┌─────────────────────────────────────────────────────┐
│              Vert.x HttpServer                      │
│  ┌───────────────────────────────────────────────┐  │
│  │  Router                                        │  │
│  │  ├── PUT /objects/{key}  → Stream to disk     │  │
│  │  ├── GET /objects/{key}  → Stream from disk   │  │
│  │  └── DELETE /objects/{key} → Remove file      │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│           Netty Async File I/O                      │
│           Chunked streaming (no full buffering)     │
└─────────────────────┬───────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│           Local Filesystem                          │
│           ./data/ directory                         │
└─────────────────────────────────────────────────────┘
```

## SSL Configuration

Two approaches are available:

### 1. PEM Files (Current Default)

Used by `Main.java` via Vert.x's built-in `PemKeyCertOptions`:

```java
HttpServerOptions options = new HttpServerOptions()
    .setSsl(true)
    .setPemKeyCertOptions(new PemKeyCertOptions()
        .setKeyPath("key.pem")
        .setCertPath("cert.pem"));
```

### 2. KeyStore (PKCS12/JKS)

`SslConfig.java` provides Netty-native SSL context creation:

```java
SslConfig sslConfig = SslConfig.fromKeyStore(
    "keystore.p12", 
    "keystorePassword", 
    "keyPassword"
);
```

Also supports mutual TLS (mTLS) via `fromKeyStoreWithTrust()`.

## Development

### Run Tests

```bash
./gradlew test

# Single test class
./gradlew test --tests "com.blob.MyTestClass"

# Single test method
./gradlew test --tests "com.blob.MyTestClass.myTestMethod"
```

### Build Without Tests

```bash
./gradlew build -x test
```

### View Dependencies

```bash
./gradlew dependencies
```

## Learning Objectives

This project serves as a hands-on exploration of:

1. **Vert.x Event Loop Model** — Understanding non-blocking I/O and the verticle model
2. **Netty ByteBuf & Channels** — Efficient memory management and zero-copy patterns
3. **TLS Performance** — Comparing JDK SSL vs. OpenSSL via netty-tcnative
4. **Streaming Architecture** — Designing for constant memory usage regardless of file size

## Future Enhancements

- [x] OCI-native API (`/o`, `/o/:objectName`)
- [x] S3-compatible API (`/:bucket` ListObjectsV2)
- [ ] Chunked file upload/download with backpressure
- [ ] S3-compatible API (`/bucket/key` paths)
- [ ] Benchmark suite for TLS throughput comparison
- [ ] Containerized deployment (Docker)
- [ ] Authentication (API keys, mTLS)

## References

- [Vert.x Web Documentation](https://vertx.io/docs/vertx-web/java/)
- [Netty User Guide](https://netty.io/wiki/user-guide.html)
- [netty-tcnative (OpenSSL)](https://github.com/netty/netty-tcnative)

## License

MIT
