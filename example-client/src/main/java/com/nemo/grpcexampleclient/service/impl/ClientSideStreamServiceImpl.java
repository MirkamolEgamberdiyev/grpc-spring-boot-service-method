package com.nemo.grpcexampleclient.service.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.protobuf.ByteString;
import com.nemo.grpcexampleclient.service.ClientSideStreamService;
import com.nemo.grpcexampleserver.BytesRequest;
import com.nemo.grpcexampleserver.ClientSideStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringRequest;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Mirkamol
 * @version 1.0
 * @date 2023/12/07
 */
@Slf4j
@Service
public class ClientSideStreamServiceImpl implements ClientSideStreamService {

    /**
     * Client streaming uses non-blocking services
     */
    @GrpcClient("server-service")
    private ClientSideStreamServiceGrpc.ClientSideStreamServiceStub serviceStub;

    @Value("${server.port}")
    protected String serverPort;

    @Value("${server.domain}")
    protected String serverDomain;

    /**
     * Client Streaming - String
     */
    @SneakyThrows
    @Override
    public String clientStreamString() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        StringBuilder result = new StringBuilder();

        StreamObserver<StringRequest> observer = serviceStub.clientStreamString(new StreamObserver<StringResponse>() {
            @Override
            public void onNext(StringResponse stringResponse) {
                log.info("ClientSideStreamServiceImpl clientStreamString onNext stringResponse:{}", stringResponse);
                result.append(stringResponse.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("ClientSideStreamServiceImpl clientStreamString onError: ", throwable);
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
                log.info("ClientSideStreamServiceImpl clientStreamString onNext onCompleted");
            }
        });

        /**
         * Send data to the server in batches
         */
        for (int i = 0; i < 10; i++) {
            StringRequest.Builder builder = StringRequest.newBuilder();
            builder.setValue("Client Streaming - String No." + i + " Send data" + System.lineSeparator());
            observer.onNext(builder.build());
        }
        observer.onCompleted();
        countDownLatch.await();

        return result.toString();
    }

    /**
     * client streaming - bytes
     *
     */
    @SneakyThrows
    @Override
    public String clientStreamBytes(MultipartFile file) {
        if (ObjectUtil.isEmpty(file)) {
            return "Please upload files";
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        BytesRequest.Builder builder = BytesRequest.newBuilder();
        builder.setFileName(file.getOriginalFilename());
        log.info("OriginalFileName: " + file.getOriginalFilename());

        StringBuilder result = new StringBuilder();
        StreamObserver<BytesRequest> observer = serviceStub.clientStreamBytes(new StreamObserver<StringResponse>() {
            @Override
            public void onNext(StringResponse stringResponse) {
                log.info("ClientSideStreamServiceImpl clientStreamBytes onNext stringResponse:{}", stringResponse);
                result.append(stringResponse.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("ClientSideStreamServiceImpl clientStreamBytes onError: ", throwable);
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
                log.info("ClientSideStreamServiceImpl clientStreamBytes onCompleted");
            }
        });

        byte[] data = file.getBytes();
        /**
         * Split uploaded files into segments
         */
        int buffLength = 512 * 1024;
        byte[][] splitData = ArrayUtil.split(data, buffLength);
        for (byte[] splitDatum : splitData) {
            observer.onNext(builder.setData(ByteString.copyFrom(splitDatum)).build());
        }
        observer.onCompleted();

        countDownLatch.await(1, TimeUnit.MINUTES);
        result.insert(0, "http://" + serverDomain + ":" + serverPort + "/");
        return result.toString();
    }

    /**
     * client streaming - bytes The server receives through byte array
     *
     */
    @SneakyThrows
    @Override
    public String clientStreamBytesByte(MultipartFile file) {
        if (ObjectUtil.isEmpty(file)) {
            return "Please upload files";
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        BytesRequest.Builder builder = BytesRequest.newBuilder();
        builder.setFileName(file.getOriginalFilename());
        log.info("OriginalFileName: " + file.getOriginalFilename());

        StringBuilder result = new StringBuilder();
        StreamObserver<BytesRequest> observer = serviceStub.clientStreamBytesByte(new StreamObserver<StringResponse>() {
            @Override
            public void onNext(StringResponse stringResponse) {
                log.info("ClientSideStreamServiceImpl clientStreamBytesByte onNext stringResponse:{}", stringResponse);
                result.append(stringResponse.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("ClientSideStreamServiceImpl clientStreamBytesByte onError: ", throwable);
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
                log.info("ClientSideStreamServiceImpl clientStreamBytesByte onCompleted");
            }
        });

        byte[] data = file.getBytes();
        /**
         * Split uploaded files into segments
         */
        int buffLength = 512 * 1024;
        byte[][] splitData = ArrayUtil.split(data, buffLength);

        /**
         * Send data to server in batches
         */
        for (byte[] splitDatum : splitData) {
            observer.onNext(builder.setData(ByteString.copyFrom(splitDatum)).build());
        }
        observer.onCompleted();

        countDownLatch.await(1, TimeUnit.MINUTES);
        result.insert(0, "http://" + serverDomain + ":" + serverPort + "/");
        return result.toString();
    }
}
