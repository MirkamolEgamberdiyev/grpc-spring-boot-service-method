package com.nemo.grpcexampleserver.service.grpc.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import com.nemo.grpcexampleserver.BytesResponse;
import com.nemo.grpcexampleserver.Empty;
import com.nemo.grpcexampleserver.ServerSideStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

/**
 * @author Mirkamol
 * @version 1.0
 * @date 2023/12/07
 */
@Slf4j
@GrpcService
public class ServerSideStreamGrpcService extends ServerSideStreamServiceGrpc.ServerSideStreamServiceImplBase {

    private final String filePath;

    @Inject
    public ServerSideStreamGrpcService(@Value("${filePath}") String filePath) {
        this.filePath = filePath;
    }

    /**
     * Server-side streaming - string
     * @param request
     * @param responseObserver
     */
    @Override
    public void serverStreamString(Empty request, StreamObserver<StringResponse> responseObserver) {
        StringResponse.Builder builder = StringResponse.newBuilder();
        // Send data to client via stream 7 times
        for (int i = 0; i < 7; i++) {
            builder.setValue("Server-side streaming - String No." + i + "Send data" + System.lineSeparator());
            responseObserver.onNext(builder.build());
        }
        responseObserver.onCompleted();
    }

    /**
     * Server-side streaming - bytes
     * @param request
     * @param responseObserver
     */
    @Override
    public void serverStreamBytes(Empty request, StreamObserver<BytesResponse> responseObserver) {
        // Read the file path in the resources directory
        ClassPathResource classPathResource = new ClassPathResource(filePath + "knight.png");

        try (FileInputStream fis = new FileInputStream(classPathResource.getFile());
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            // 512K Buffer size, that is, the size of data sent to the client each time
            byte[] buffer = new byte[512 * 1024];
            while (bis.read(buffer) > 0) {
                responseObserver.onNext(BytesResponse.newBuilder().setData(ByteString.copyFrom(buffer)).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("File size：" + FileUtil.readableFileSize(classPathResource.getFile()));
        responseObserver.onCompleted();
    }
}
