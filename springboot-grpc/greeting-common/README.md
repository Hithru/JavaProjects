# greeting-common

This module owns the language-neutral gRPC contract. It deliberately contains no Spring Boot application code, so a server, client, or another service can reuse the same generated request, response, and service types.

## What is in this module

- `src/main/proto/greeting.proto` declares the `greeting.GreetingService` service.
- `GreetingRequest` and `GreetingResponse` each contain one Protobuf `string message` field.
- `greetingMethod` is a unary RPC: one request produces one response.
- `java_package` places generated Java types in `com.example.greeting.grpc`.
- `java_multiple_files` creates a separate Java source file for each message and service type.

## Code generation

The Maven build uses `os-maven-plugin` to identify the current operating system and CPU architecture. `protobuf-maven-plugin` then selects the matching `protoc` and `protoc-gen-grpc-java` executables.

During `mvn compile`, Maven runs both generation goals:

- `compile` generates Protobuf message classes such as `GreetingRequest` and `GreetingResponse`.
- `compile-custom` generates gRPC code such as `GreetingServiceGrpc` and its server base class.

Generated sources appear under `target/generated-sources/protobuf/`. They are build output and should not be edited or committed. Change the `.proto` contract and rebuild instead.

## Build only this module

From the root `springboot-grpc` directory:

```powershell
mvn -pl greeting-common clean install
```

The installed JAR is then available to `greeting-service` and any future client module.

