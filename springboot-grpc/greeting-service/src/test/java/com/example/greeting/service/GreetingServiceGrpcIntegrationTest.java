package com.example.greeting.service;

import com.example.greeting.grpc.GreetingRequest;
import com.example.greeting.grpc.GreetingResponse;
import com.example.greeting.grpc.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreetingServiceGrpcIntegrationTest {

    private Server server;
    private ManagedChannel channel;

    @BeforeEach
    void startInProcessServer() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new GreetingServiceImpl())
                .build()
                .start();
        channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();
    }

    @AfterEach
    void stopInProcessServer() {
        channel.shutdownNow();
        server.shutdownNow();
    }

    @Test
    void generatedBlockingStubCallsUnaryRpc() {
        GreetingResponse response = GreetingServiceGrpc.newBlockingStub(channel)
                .greetingMethod(request("Unary over a generated stub"));

        assertEquals("Hello from server - received your message: "
                + "[Unary over a generated stub]", response.getMessage());
    }

    @Test
    void generatedBlockingStubConsumesServerStream() {
        Iterator<GreetingResponse> responses = GreetingServiceGrpc.newBlockingStub(channel)
                .greetingStream(request("Stream over a generated stub"));
        List<Long> sequences = new ArrayList<>();
        responses.forEachRemaining(response -> sequences.add(response.getSequence()));

        assertEquals(List.of(1L, 2L, 3L), sequences);
    }

    @Test
    void generatedAsyncStubSendsClientStream() throws InterruptedException {
        AwaitingObserver<GreetingResponse> responseObserver = new AwaitingObserver<>();
        StreamObserver<GreetingRequest> requestObserver = GreetingServiceGrpc.newStub(channel)
                .collectGreetings(responseObserver);

        requestObserver.onNext(request("First"));
        requestObserver.onNext(request("Second"));
        requestObserver.onCompleted();

        assertTrue(responseObserver.await());
        assertNull(responseObserver.error);
        assertEquals(2, responseObserver.values.get(0).getSequence());
    }

    @Test
    void generatedAsyncStubExchangesBidirectionalStream() throws InterruptedException {
        AwaitingObserver<GreetingResponse> responseObserver = new AwaitingObserver<>();
        StreamObserver<GreetingRequest> requestObserver = GreetingServiceGrpc.newStub(channel)
                .greetingChat(responseObserver);

        requestObserver.onNext(request("First chat message"));
        requestObserver.onNext(request("Second chat message"));
        requestObserver.onCompleted();

        assertTrue(responseObserver.await());
        assertNull(responseObserver.error);
        assertEquals(List.of(1L, 2L), responseObserver.values.stream()
                .map(GreetingResponse::getSequence)
                .toList());
    }

    private GreetingRequest request(String message) {
        return GreetingRequest.newBuilder()
                .setSender("Integration test")
                .setMessage(message)
                .build();
    }

    private static final class AwaitingObserver<T> implements StreamObserver<T> {
        private final List<T> values = new ArrayList<>();
        private final CountDownLatch terminalEvent = new CountDownLatch(1);
        private Throwable error;

        @Override
        public void onNext(T value) {
            values.add(value);
        }

        @Override
        public void onError(Throwable throwable) {
            error = throwable;
            terminalEvent.countDown();
        }

        @Override
        public void onCompleted() {
            terminalEvent.countDown();
        }

        private boolean await() throws InterruptedException {
            return terminalEvent.await(1, TimeUnit.SECONDS);
        }
    }
}
