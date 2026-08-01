package com.example.greeting.service;

import com.example.greeting.grpc.GreetingRequest;
import com.example.greeting.grpc.GreetingResponse;
import com.example.greeting.grpc.GreetingServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GRpcService
public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GreetingServiceImpl.class);

    @Override
    public void greetingMethod(
            GreetingRequest request,
            StreamObserver<GreetingResponse> responseObserver) {
        String message = request.getMessage();
        LOGGER.info("Received greeting message: {}", message);

        GreetingResponse response = GreetingResponse.newBuilder()
                .setMessage("Hello from server - received your message: [" + message + "]")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

