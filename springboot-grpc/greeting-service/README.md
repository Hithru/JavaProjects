# greeting-service

This module is the runnable Spring Boot gRPC server. It depends on `greeting-common`, so its Java implementation compiles against types generated directly from the shared Protobuf contract.

## What is in this module

- `GreetingServiceApplication` starts Spring Boot and the embedded gRPC server.
- `GreetingServiceImpl` extends the generated `GreetingServiceGrpc.GreetingServiceImplBase` server skeleton.
- `@GRpcService` makes the LogNet starter discover and register the implementation automatically.
- `greetingMethod` logs the request, sends one `GreetingResponse`, and completes the `StreamObserver`.
- `application.yml` uses port `6565` and enables server reflection so `grpcurl` can discover the contract without a local `.proto` argument.

The RPC response format is:

```text
Hello from server - received your message: [the client message]
```

## Run this module

First build the whole reactor from the root directory so `greeting-common` is generated and installed:

```powershell
mvn clean install
mvn -pl greeting-service spring-boot:run
```

Alternatively, run the packaged executable JAR after the build:

```powershell
java -jar greeting-service/target/greeting-service-1.0.0-SNAPSHOT.jar
```

The gRPC server listens on `localhost:6565`. This project does not start an HTTP REST endpoint.

## Test with grpcurl

With the server running:

```powershell
grpcurl -plaintext -d '{"message":"Tech Primers"}' localhost:6565 greeting.GreetingService/greetingMethod
```

Expected response:

```json
{
  "message": "Hello from server - received your message: [Tech Primers]"
}
```

