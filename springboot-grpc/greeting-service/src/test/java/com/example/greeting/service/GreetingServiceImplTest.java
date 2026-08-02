package com.example.greeting.service;

import com.example.greeting.grpc.GreetingRequest;
import com.example.greeting.grpc.GreetingResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreetingServiceImplTest {

    private final GreetingServiceImpl service = new GreetingServiceImpl();

    @Test
    void unaryGreetingReturnsOneResponse() {
        TestObserver<GreetingResponse> observer = new TestObserver<>();

        service.greetingMethod(request("Developer", "Hello"), observer);

        assertTrue(observer.completed);
        assertNull(observer.error);
        assertEquals(1, observer.values.size());
        assertEquals("Hello from server - received your message: [Hello]",
                observer.values.get(0).getMessage());
        assertEquals(1, observer.values.get(0).getSequence());
    }

    @Test
    void blankUnaryMessageReturnsInvalidArgument() {
        TestObserver<GreetingResponse> observer = new TestObserver<>();

        service.greetingMethod(request("Developer", "  "), observer);

        StatusRuntimeException error = assertInstanceOf(StatusRuntimeException.class, observer.error);
        assertEquals(Status.Code.INVALID_ARGUMENT, error.getStatus().getCode());
        assertEquals("message must not be blank", error.getStatus().getDescription());
    }

    @Test
    void serverStreamReturnsThreeOrderedResponses() {
        TestObserver<GreetingResponse> observer = new TestObserver<>();

        service.greetingStream(request("Developer", "Stream this"), observer);

        assertTrue(observer.completed);
        assertEquals(List.of(1L, 2L, 3L),
                observer.values.stream().map(GreetingResponse::getSequence).toList());
    }

    @Test
    void clientStreamAggregatesRequestsIntoOneResponse() {
        TestObserver<GreetingResponse> responseObserver = new TestObserver<>();
        StreamObserver<GreetingRequest> requestObserver = service.collectGreetings(responseObserver);

        requestObserver.onNext(request("Developer", "Hello"));
        requestObserver.onNext(request("Developer", "from the client stream"));
        requestObserver.onCompleted();

        assertTrue(responseObserver.completed);
        assertEquals(1, responseObserver.values.size());
        assertEquals(2, responseObserver.values.get(0).getSequence());
        assertEquals("Server collected 2 messages from [Developer]: "
                        + "[Hello | from the client stream]",
                responseObserver.values.get(0).getMessage());
    }

    @Test
    void bidirectionalStreamRepliesToEveryRequest() {
        TestObserver<GreetingResponse> responseObserver = new TestObserver<>();
        StreamObserver<GreetingRequest> requestObserver = service.greetingChat(responseObserver);

        requestObserver.onNext(request("Developer", "First"));
        requestObserver.onNext(request("Developer", "Second"));
        requestObserver.onCompleted();

        assertTrue(responseObserver.completed);
        assertEquals(2, responseObserver.values.size());
        assertEquals("Chat reply to [Developer]: [First]",
                responseObserver.values.get(0).getMessage());
        assertEquals("Chat reply to [Developer]: [Second]",
                responseObserver.values.get(1).getMessage());
    }

    private GreetingRequest request(String sender, String message) {
        return GreetingRequest.newBuilder()
                .setSender(sender)
                .setMessage(message)
                .build();
    }

    private static final class TestObserver<T> implements StreamObserver<T> {
        private final List<T> values = new ArrayList<>();
        private Throwable error;
        private boolean completed;

        @Override
        public void onNext(T value) {
            values.add(value);
        }

        @Override
        public void onError(Throwable throwable) {
            error = throwable;
        }

        @Override
        public void onCompleted() {
            completed = true;
        }
    }
}
