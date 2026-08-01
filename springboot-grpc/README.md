# Spring Boot gRPC multi-module example

This project implements a unary gRPC greeting server with Spring Boot, Protobuf-generated messages, and a generated gRPC server skeleton. Its split mirrors the architecture commonly shown in the Tech Primers tutorial: one reusable contract module and one runnable server module.

## Project structure

```text
springboot-grpc/
|-- pom.xml                         Parent and dependency/version management
|-- greeting-common/
|   |-- pom.xml                     Protobuf and gRPC code-generation build
|   `-- src/main/proto/
|       `-- greeting.proto          Shared API contract
`-- greeting-service/
    |-- pom.xml                     Spring Boot server dependencies
    `-- src/main/
        |-- java/com/example/greeting/service/
        |   |-- GreetingServiceApplication.java
        |   `-- GreetingServiceImpl.java
        `-- resources/application.yml
```

Each module also has its own README describing its responsibility and implementation details:

- [`greeting-common/README.md`](greeting-common/README.md)
- [`greeting-service/README.md`](greeting-service/README.md)

## How the pieces connect

1. `greeting.proto` defines the wire contract and the unary `greetingMethod` RPC.
2. Building `greeting-common` invokes `protoc` to generate message classes and `protoc-gen-grpc-java` to generate `GreetingServiceGrpc`.
3. Maven packages those generated classes in the `greeting-common` JAR.
4. `greeting-service` depends on that JAR and extends the generated `GreetingServiceImplBase` skeleton.
5. The LogNet Spring Boot starter discovers `GreetingServiceImpl` through `@GRpcService` and serves it on port `6565`.

Versions are centralized in the root POM. The gRPC BOM pins every gRPC library to `1.71.0`, the line supported by LogNet starter `5.2.0`, to avoid dependency-management mismatches.

## Prerequisites

- JDK 17 or newer
- Maven 3.9 or newer
- `grpcurl` for the command-line smoke test

You do not need to install `protoc` manually. Maven downloads the executable matching your operating system and processor.

## 1. Generate sources and build everything

Open a terminal in this directory and run:

```powershell
mvn clean install
```

This command builds modules in dependency order. Generated files can be inspected at:

```text
greeting-common/target/generated-sources/protobuf/java
greeting-common/target/generated-sources/protobuf/grpc-java
```

Do not edit generated files; update `greeting.proto` and rebuild.

## 2. Start the server

After the successful install, run:

```powershell
mvn -pl greeting-service spring-boot:run
```

Wait until the logs report that the gRPC server is listening on port `6565`.

## 3. Invoke the endpoint

In another terminal, list the reflected services if desired:

```powershell
grpcurl -plaintext localhost:6565 list
```

Call the greeting RPC:

```powershell
grpcurl -plaintext -d '{"message":"Tech Primers"}' localhost:6565 greeting.GreetingService/greetingMethod
```

Expected response:

```json
{
  "message": "Hello from server - received your message: [Tech Primers]"
}
```

`-plaintext` is appropriate for this local example because TLS is not configured. Production deployments should configure transport security and omit `-plaintext`.

## Clean rebuild

Whenever the Protobuf contract changes, regenerate everything with:

```powershell
mvn clean install
```

The `clean` phase removes old generated sources, preventing stale contract classes from remaining in the build.
