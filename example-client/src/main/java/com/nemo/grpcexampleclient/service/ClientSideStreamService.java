package com.nemo.grpcexampleclient.service;

import org.springframework.web.multipart.MultipartFile;
/**
 * @author Mirkamol
 * @version 1.0
 * @date 2023/12/07
 */
public interface ClientSideStreamService {
    /**
     * Client Streaming - String
     */
    String clientStreamString();

    /**
     * client streaming - bytes
     */
    String clientStreamBytes(MultipartFile file);

    /**
     * Client streaming - bytes server receives through byte array
     */
    String clientStreamBytesByte(MultipartFile file);
}
