# Spring Boot gRPC showcase

This is a contract-first, multi-module Spring Boot application that demonstrates all four gRPC communication models. The project keeps the Protobuf API separate from the running server, following the reusable contract architecture popularized by the Tech Primers Spring Boot gRPC tutorial.

## What is gRPC?

gRPC is a remote procedure call framework. Instead of designing URLs and exchanging hand-written JSON documents as in a typical REST API, you describe services and messages in a `.proto` contract. The Protobuf compiler generates strongly typed Java messages, client stubs, and server base classes from that contract.

gRPC commonly uses:

- Protocol Buffers, a compact binary serialization format with explicit field numbers.
- HTTP/2, which provides multiplexed requests and long-lived bidirectional streams.
- Generated APIs, so clients call methods rather than manually building HTTP requests.
- Standard status codes such as `INVALID_ARGUMENT`, `NOT_FOUND`, and `UNAVAILABLE`.

The service is still remote: a generated stub hides much of the networking, serialization, and response-handling code, but deadlines, cancellation, failures, and compatibility still need deliberate design.

## Modules and build flow

```text
greeting.proto
      |
      | protoc + protoc-gen-grpc-java
      v
greeting-common JAR
  - Protobuf message classes
  - client stubs
  - server base class
      |
      | Maven dependency
      v
greeting-service
  - Spring Boot application
  - @GRpcService implementation
  - embedded Netty gRPC server on port 6565
```

```text
springboot-grpc/
|-- pom.xml                         Reactor and dependency management
|-- greeting-common/
|   |-- pom.xml                     Protobuf/gRPC generation configuration
|   |-- README.md                   Contract module guide
|   `-- src/main/proto/greeting.proto
`-- greeting-service/
    |-- pom.xml                     Spring Boot server dependencies
    |-- README.md                   Server and RPC guide
    `-- src/
        |-- main/java/...           Application and service implementation
        |-- main/resources/         Server configuration
        `-- test/java/...           Unit and in-process transport tests
```

Read the module-specific documentation for more detail:

- [`greeting-common/README.md`](greeting-common/README.md)
- [`greeting-service/README.md`](greeting-service/README.md)

## RPC styles demonstrated

| RPC | Request flow | Response flow | Typical use |
| --- | --- | --- | --- |
| `greetingMethod` | One | One | Normal request/response operations |
| `greetingStream` | One | Stream | Progress, feeds, or a large result set |
| `collectGreetings` | Stream | One | Uploading chunks, telemetry, or batches |
| `greetingChat` | Stream | Stream | Chat and real-time collaboration |

Every response includes a `sequence` field so ordering is visible. Requests also include an optional `sender`; the server uses `anonymous` when it is absent.

## Prerequisites

- JDK 17 or newer
- Maven 3.9 or newer
- `grpcurl` for command-line calls

Maven downloads the correct `protoc` executables for the current operating system, so a separate Protobuf compiler installation is not required.

## Build, generate, and test

From this directory:

```powershell
mvn clean install
```

The build order is `greeting-common` followed by `greeting-service`. Generated source can be inspected under:

```text
greeting-common/target/generated-sources/protobuf/java
greeting-common/target/generated-sources/protobuf/grpc-java
```

Generated files are build output. Change `greeting.proto` and rebuild instead of editing them.

Run just the automated tests with:

```powershell
mvn test
```

The test suite has two layers:

- Service-level tests exercise observer completion, ordering, aggregation, and status errors.
- In-process integration tests start a real gRPC server and call it through the generated blocking and asynchronous client stubs. Protobuf serialization and generated RPC bindings are exercised without using a TCP port.

## Start the server

After `mvn clean install`:

```powershell
mvn -pl greeting-service spring-boot:run
```

Or run the executable JAR:

```powershell
java -jar greeting-service/target/greeting-service-1.0.0-SNAPSHOT.jar
```

The server listens on `localhost:6565`. Reflection is enabled for local learning, allowing `grpcurl` to discover services without receiving the `.proto` file separately.

## Explore with grpcurl

List services and methods:

```powershell
grpcurl -plaintext localhost:6565 list
grpcurl -plaintext localhost:6565 list greeting.GreetingService
```

### Unary call

```powershell
grpcurl -plaintext `
  -d '{"sender":"Developer","message":"Hello unary gRPC"}' `
  localhost:6565 greeting.GreetingService/greetingMethod
```

### Server-streaming call

One request produces three responses:

```powershell
grpcurl -plaintext `
  -d '{"sender":"Developer","message":"Show me a stream"}' `
  localhost:6565 greeting.GreetingService/greetingStream
```

### Client-streaming call

Pipe multiple JSON objects into `grpcurl`; the server returns one aggregate response after input ends:

```powershell
@'
{"sender":"Developer","message":"First item"}
{"sender":"Developer","message":"Second item"}
'@ | grpcurl -plaintext -d '@' localhost:6565 greeting.GreetingService/collectGreetings
```

### Bidirectional-streaming call

Each input produces a response on the same open call:

```powershell
@'
{"sender":"Developer","message":"First chat message"}
{"sender":"Developer","message":"Second chat message"}
'@ | grpcurl -plaintext -d '@' localhost:6565 greeting.GreetingService/greetingChat
```

### Error handling

Blank messages are rejected using the gRPC-native `INVALID_ARGUMENT` status:

```powershell
grpcurl -plaintext `
  -d '{"sender":"Developer","message":""}' `
  localhost:6565 greeting.GreetingService/greetingMethod
```

## Protobuf compatibility rules

The number after each field, such as `message = 1`, is its permanent wire identifier. Once clients use a contract:

- Do not change an existing field number.
- Do not reuse a removed field number; mark it `reserved`.
- Adding a new optional/default-valued field with a new number is generally compatible.
- Package, service, and method names form part of the callable gRPC path.

These rules let old and new clients coexist during gradual deployments.

## Local example versus production

`-plaintext` and server reflection make local exploration convenient. A production service should normally add TLS, authentication/authorization, deadlines, request-size limits, observability, and a policy for retries and idempotency. Streaming implementations should also account for cancellation and flow control when producing large or unbounded streams.

## Maven certificate troubleshooting

If Maven reports `PKIX path building failed` while contacting Maven Central, the JDK used by Maven does not trust the certificate presented by the local network or proxy. Check the active JDK with `mvn -version`, then configure that JDK/Maven installation with the organization's trusted CA certificate or ask the network administrator for the correct proxy setup. Avoid disabling Maven's TLS certificate validation, because that makes dependency downloads vulnerable to tampering.
