package com.nemo.grpcexampleserver.service.grpc.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import com.nemo.grpcexampleserver.BytesRequest;
import com.nemo.grpcexampleserver.ClientSideStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringRequest;
import com.nemo.grpcexampleserver.StringResponse;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Mirkamol
 * @version 1.0
 * @date 2023/12/07
 */
@Slf4j
@GrpcService
public class ClientSideStreamGrpcService extends ClientSideStreamServiceGrpc.ClientSideStreamServiceImplBase {

    private final String uploadFilePath;

    @Inject
    public ClientSideStreamGrpcService(@Value("${uploadFilePath}") String uploadFilePath) {
        this.uploadFilePath = uploadFilePath;
    }

    /**
     * Client Streaming - String
     *
     * @param responseObserver
     */
    @Override
    public StreamObserver<StringRequest> clientStreamString(StreamObserver<StringResponse> responseObserver) {
        // Use StringBuilder to receive data from the client multiple times
        StringBuilder stringBuilder = new StringBuilder();

        return new StreamObserver<StringRequest>() {
            @Override
            public void onNext(StringRequest request) {
                log.info("clientStreamString onNext request={}", request.getValue());
                // Splicing data transmitted in batches by the client
                stringBuilder.append(request.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("clientStreamString onError: ", throwable);
            }

            @Override
            public void onCompleted() {
                String requestString = stringBuilder.toString();
                StringResponse result = StringResponse.newBuilder().setValue("clientStreamString request parameters: " + requestString).build();
                responseObserver.onNext(result);
                responseObserver.onCompleted();
            }
        };
    }

    /**
     * Client streaming - bytes returns the save path of the uploaded file
     *
     * @param responseObserver
     */
    @SneakyThrows
    @Override
    public StreamObserver<BytesRequest> clientStreamBytes(StreamObserver<StringResponse> responseObserver) {
        File uploadFile = new File(uploadFilePath + System.currentTimeMillis());
        // Use the BufferedOutputStream stream to receive data from the client multiple times
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(uploadFile));
        StringBuilder fileName = new StringBuilder();


        return new StreamObserver<BytesRequest>() {
            @SneakyThrows
            @Override
            public void onNext(BytesRequest bytesRequest) {
                log.info("clientStreamBytes onNext.");
                // Splicing data transmitted in batches by the client
                bufferedOutputStream.write(bytesRequest.getData().toByteArray());
                if (StringUtils.isEmpty(fileName.toString())) {
                    fileName.append(bytesRequest.getFileName());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("clientStreamBytes onError: ", throwable);
            }

            @SneakyThrows
            @Override
            public void onCompleted() {
                log.info("clientStreamBytes onCompleted.");
                // Flush the data saved in the stream to the file
                bufferedOutputStream.flush();
                bufferedOutputStream.close();

                // Rename the file, retaining the extension of the source file
                File file = FileUtil.rename(uploadFile, System.currentTimeMillis() + fileName.toString(), true, true);

                StringResponse.Builder resultBuilder = StringResponse.newBuilder();
                resultBuilder.setValue(file.getName());
                responseObserver.onNext(resultBuilder.build());
                responseObserver.onCompleted();
            }
        };
    }

    /**
     * Client streaming - bytes server receives through byte array
     *
     * @param responseObserver
     */
    @Override
    public StreamObserver<BytesRequest> clientStreamBytesByte(StreamObserver<StringResponse> responseObserver) {
        StringBuilder fileName = new StringBuilder();
        byte[][] buff = {null};

        return new StreamObserver<BytesRequest>() {
            @Override
            public void onNext(BytesRequest bytesRequest) {
                log.info("clientStreamBytesByte onNext.");
                // Splicing data transmitted multiple times from the client
                buff[0] = ArrayUtil.addAll(buff[0], bytesRequest.getData().toByteArray());
                if (StringUtils.isEmpty(fileName.toString())) {
                    fileName.append(bytesRequest.getFileName());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("clientStreamBytesByte onError: {}", throwable);
            }

            @SneakyThrows
            @Override
            public void onCompleted() {
                log.info("clientStreamBytesByte onCompleted.");
                byte[] data = buff[0];
                File uploadFile = new File(uploadFilePath + System.currentTimeMillis() + fileName.toString());
                // Convert the byte array into a file and store it in the target path
                FileOutputStream fos = new FileOutputStream(uploadFile);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                IoUtil.copy(byteArrayInputStream, fos);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fos);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                bufferedOutputStream.close();
                fos.close();

                responseObserver.onNext(StringResponse.newBuilder().setValue(uploadFile.getName()).build());
                responseObserver.onCompleted();
            }
        };
    }
}
