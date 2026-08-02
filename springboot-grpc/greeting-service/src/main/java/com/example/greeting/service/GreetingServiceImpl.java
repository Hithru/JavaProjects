package com.example.greeting.service;

import com.example.greeting.grpc.GreetingRequest;
import com.example.greeting.grpc.GreetingResponse;
import com.example.greeting.grpc.GreetingServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringJoiner;

@GRpcService
public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GreetingServiceImpl.class);

    @Override
    public void greetingMethod(
            GreetingRequest request,
            StreamObserver<GreetingResponse> responseObserver) {
        if (rejectBlankMessage(request, responseObserver)) {
            return;
        }

        String message = request.getMessage();
        LOGGER.info("Unary greeting received from {}: {}", senderOf(request), message);

        GreetingResponse response = GreetingResponse.newBuilder()
                .setMessage("Hello from server - received your message: [" + message + "]")
                .setSequence(1)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void greetingStream(
            GreetingRequest request,
            StreamObserver<GreetingResponse> responseObserver) {
        if (rejectBlankMessage(request, responseObserver)) {
            return;
        }

        LOGGER.info("Server-streaming greeting requested by {}", senderOf(request));
        for (long sequence = 1; sequence <= 3; sequence++) {
            responseObserver.onNext(response(
                    "Stream greeting " + sequence + " for [" + senderOf(request)
                            + "] - received: [" + request.getMessage() + "]",
                    sequence));
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GreetingRequest> collectGreetings(
            StreamObserver<GreetingResponse> responseObserver) {
        return new StreamObserver<>() {
            private final StringJoiner messages = new StringJoiner(" | ");
            private String sender = "anonymous";
            private long count;
            private boolean terminated;

            @Override
            public void onNext(GreetingRequest request) {
                if (terminated || rejectBlankMessage(request, responseObserver)) {
                    terminated = true;
                    return;
                }

                sender = senderOf(request);
                messages.add(request.getMessage());
                count++;
                LOGGER.info("Client-stream item {} received from {}", count, sender);
            }

            @Override
            public void onError(Throwable throwable) {
                terminated = true;
                LOGGER.warn("Client-streaming call cancelled by the client", throwable);
            }

            @Override
            public void onCompleted() {
                if (terminated) {
                    return;
                }
                if (count == 0) {
                    responseObserver.onError(invalidArgument("at least one message is required"));
                    terminated = true;
                    return;
                }

                responseObserver.onNext(response(
                        "Server collected " + count + " messages from [" + sender
                                + "]: [" + messages + "]",
                        count));
                responseObserver.onCompleted();
                terminated = true;
            }
        };
    }

    @Override
    public StreamObserver<GreetingRequest> greetingChat(
            StreamObserver<GreetingResponse> responseObserver) {
        return new StreamObserver<>() {
            private long sequence;
            private boolean terminated;

            @Override
            public void onNext(GreetingRequest request) {
                if (terminated || rejectBlankMessage(request, responseObserver)) {
                    terminated = true;
                    return;
                }

                sequence++;
                LOGGER.info("Chat message {} received from {}", sequence, senderOf(request));
                responseObserver.onNext(response(
                        "Chat reply to [" + senderOf(request) + "]: [" + request.getMessage() + "]",
                        sequence));
            }

            @Override
            public void onError(Throwable throwable) {
                terminated = true;
                LOGGER.warn("Bidirectional chat cancelled by the client", throwable);
            }

            @Override
            public void onCompleted() {
                if (!terminated) {
                    responseObserver.onCompleted();
                    terminated = true;
                }
            }
        };
    }

    private boolean rejectBlankMessage(
            GreetingRequest request,
            StreamObserver<?> responseObserver) {
        if (!request.getMessage().isBlank()) {
            return false;
        }

        responseObserver.onError(invalidArgument("message must not be blank"));
        return true;
    }

    private StatusRuntimeException invalidArgument(String description) {
        return Status.INVALID_ARGUMENT
                .withDescription(description)
                .asRuntimeException();
    }

    private GreetingResponse response(String message, long sequence) {
        return GreetingResponse.newBuilder()
                .setMessage(message)
                .setSequence(sequence)
                .build();
    }

    private String senderOf(GreetingRequest request) {
        return request.getSender().isBlank() ? "anonymous" : request.getSender();
    }
}
