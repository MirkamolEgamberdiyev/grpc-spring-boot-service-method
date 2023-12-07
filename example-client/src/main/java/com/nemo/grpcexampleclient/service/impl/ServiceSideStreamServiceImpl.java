package com.nemo.grpcexampleclient.service.impl;

import com.nemo.grpcexampleclient.service.ServerSideStreamService;
import com.nemo.grpcexampleserver.BytesResponse;
import com.nemo.grpcexampleserver.Empty;
import com.nemo.grpcexampleserver.ServerSideStreamServiceGrpc;
import com.nemo.grpcexampleserver.StringResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

/**
 * @author Mirkamol
 * @version 1.0
 * @date 2023/12/07
 */
@Slf4j
@Service
public class ServiceSideStreamServiceImpl implements ServerSideStreamService {

    @GrpcClient("server-service")
    private ServerSideStreamServiceGrpc.ServerSideStreamServiceBlockingStub blockingStub;

    /**
     * Server-side streaming - string
     */
    @Override
    public String serverStreamString() {
        /**
         * The data obtained through the server-side streaming interface is a type that inherits the Iterator interface.
         */
        Iterator<StringResponse> responseIterator = blockingStub.serverStreamString(Empty.newBuilder().build());
        StringBuilder resultBuilder = new StringBuilder();
        while (responseIterator.hasNext()) {
            resultBuilder.append(responseIterator.next().getValue());
        }
        return resultBuilder.toString();
    }

    /**
     * Server-side streaming - bytes
     */
    @Override
    public File serverStreamBytes() {

        /**
         * step1. Receive the data returned by the server. The data obtained through the server streaming interface is a type that inherits the Iterator interface.
         */
        Iterator<BytesResponse> responseIterator = blockingStub.serverStreamBytes(Empty.newBuilder().build());
        String bufferFilePath = "";
        BufferedOutputStream bufferedOutputStream = null;
        while (responseIterator.hasNext()) {
            BytesResponse next = responseIterator.next();
            /**
             * step2. Write the segmented data into the bufferOutputStream stream
             */
            try {
                if (StringUtils.isEmpty(bufferFilePath)) {
                    bufferFilePath = "./" + UUID.randomUUID().toString() + "serverStreamBytes";
                    FileOutputStream fos = new FileOutputStream(bufferFilePath);
                    bufferedOutputStream = new BufferedOutputStream(fos);
                }
                bufferedOutputStream.write(next.getData().toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (bufferedOutputStream != null) {
                /**
                 * step3. After receiving all the data, flush the data to the file (bufferFilePath)
                 */
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(bufferFilePath);
    }
}
