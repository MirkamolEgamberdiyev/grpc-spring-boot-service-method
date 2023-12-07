package com.nemo.grpcexampleserver.service.grpc.impl;

import com.nemo.grpcexampleserver.BidirectionalStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringRequest;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mirkamol
 * @version 1.0
 * @date 2023/12/07
 */
@Slf4j
@GrpcService
public class BidirectionalStreamGrpcService extends BidirectionalStreamServiceGrpc.BidirectionalStreamServiceImplBase {

    /**
     * Bidirectional Streaming - String
     *
     * @param responseObserver
     */
    @Override
    public StreamObserver<StringRequest> bidirectionalStreamString(StreamObserver<StringResponse> responseObserver) {
        List<String> resultList = new ArrayList<>();

        return new StreamObserver<StringRequest>() {
            @Override
            public void onNext(StringRequest request) {
                log.info("BidirectionalStreamGrpcService bidirectionalStreamString onNext.");
                // Receive data from the client in batches and process it
                String name = request.getValue();
                String onNext = "welcome" + name + "Enter the live broadcast room." + System.lineSeparator();
                log.info(onNext);
                resultList.add(onNext);
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("BidirectionalStreamGrpcService bidirectionalStreamString onError {}.", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("BidirectionalStreamGrpcService bidirectionalStreamString onCompleted.");
                StringResponse.Builder builder = StringResponse.newBuilder();
                // Send data to client in batches
                for (String s : resultList) {
                    responseObserver.onNext(builder.setValue(s).build());
                }
                responseObserver.onCompleted();
            }
        };
    }
}
