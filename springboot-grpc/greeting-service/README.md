# greeting-service

This module runs the Spring Boot gRPC server. It depends on `greeting-common`, extends the generated server skeleton, and lets the LogNet starter manage the embedded Netty server lifecycle.

## Components

- `GreetingServiceApplication` is the Spring Boot entry point.
- `GreetingServiceImpl` extends `GreetingServiceGrpc.GreetingServiceImplBase`.
- `@GRpcService` registers the implementation as a Spring bean and gRPC service.
- `application.yml` configures port `6565` and enables reflection for `grpcurl`.
- `GreetingServiceImplTest` tests service logic, observer behavior, and status errors.
- `GreetingServiceGrpcIntegrationTest` starts an in-process server and invokes every RPC through generated client stubs.

`grpc-inprocess` is intentionally a runtime dependency as well as a test utility. LogNet's auto-configuration references `InProcessServerBuilder`, so the class must be available when Spring inspects that configuration.

Unlike a REST controller, this class does not return objects directly. It writes messages or errors to a `StreamObserver` and calls `onCompleted()` when that response stream is finished.

## Implemented RPC behavior

### Unary

`greetingMethod` receives one request, logs it, sends the original tutorial-style response, and completes the call.

### Server streaming

`greetingStream` receives one request and sends three sequenced responses. The demonstration produces them synchronously; a real feed could emit asynchronously and should honor cancellation and transport readiness.

### Client streaming

`collectGreetings` returns a request observer. Each `onNext` adds an incoming message to an aggregate. When the client calls `onCompleted`, the server sends one summary response.

### Bidirectional streaming

`greetingChat` returns a request observer and sends a response for every received message. Client and server streams are independent in the gRPC model, even though this simple example responds immediately to each input.

### Validation and status errors

All methods reject blank messages using `Status.INVALID_ARGUMENT`. Sending `onError` ends that RPC; no subsequent messages or completion signal should be written to the response observer.

## Build and run

From the root `springboot-grpc` directory:

```powershell
mvn clean install
mvn -pl greeting-service spring-boot:run
```

Or run the packaged server:

```powershell
java -jar greeting-service/target/greeting-service-1.0.0-SNAPSHOT.jar
```

This is a gRPC endpoint on `localhost:6565`, not an HTTP/JSON REST endpoint.

## Tests

Run this module and its required contract module tests with:

```powershell
mvn -pl greeting-service -am test
```

The service-level tests use an in-memory `StreamObserver`. The integration tests use gRPC's in-process server and channel, exercising generated stubs and Protobuf serialization without opening a network port. The root README contains live `grpcurl` smoke tests for the actual Netty HTTP/2 server.
