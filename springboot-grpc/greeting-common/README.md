# greeting-common

This module is the reusable API contract. It contains no running Spring Boot application and no business logic. A server or future client can depend on its JAR and use the exact same generated types.

## Protobuf contract

`src/main/proto/greeting.proto` uses Protobuf 3 syntax and defines:

- `GreetingRequest`, containing `message` and optional `sender` strings.
- `GreetingResponse`, containing a response `message` and numeric `sequence`.
- `GreetingService`, demonstrating unary, server-streaming, client-streaming, and bidirectional-streaming RPCs.

The Protobuf `package greeting` determines the fully qualified service name used on the wire: `greeting.GreetingService`. The `java_package` option independently places generated Java classes in `com.example.greeting.grpc`.

`java_multiple_files = true` generates top-level Java classes such as `GreetingRequest`, `GreetingResponse`, and `GreetingServiceGrpc` rather than nesting every type in one outer class.

## Stub and skeleton generation

Maven runs two native tools during the build:

1. `protoc` generates immutable Java message classes, builders, parsers, and serializers.
2. `protoc-gen-grpc-java` generates `GreetingServiceGrpc`, including client stubs and `GreetingServiceImplBase`, the server skeleton extended by `greeting-service`.

The generated client APIs include asynchronous, blocking, and future-style stubs where the RPC shape supports them. A streaming client normally uses the asynchronous stub because messages arrive over time.

`os-maven-plugin` provides `${os.detected.classifier}`, allowing Maven to download the compiler executable matching Windows, Linux, or macOS and the current CPU architecture.

## Build this module

From the root project directory:

```powershell
mvn -pl greeting-common clean install
```

Generated files appear under:

```text
greeting-common/target/generated-sources/protobuf/java
greeting-common/target/generated-sources/protobuf/grpc-java
```

Never edit generated sources directly. Update `greeting.proto`, then rebuild.

## Contract evolution

Field numbers are wire identifiers, not display order. Preserve them once published. If a field is removed, reserve its old name and number so they cannot be accidentally reused. New fields should receive new numbers, allowing older consumers to ignore values they do not understand.
