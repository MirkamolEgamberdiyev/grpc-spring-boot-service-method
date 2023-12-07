package com.nemo.grpcexampleclient.service;

import java.io.File;

/**
 * @author Mirkamol
 * @version 1.0
 * @date 2023/12/07
 */
public interface ServerSideStreamService {

    /**
     * Server-side streaming - string
     */
    String serverStreamString();

    /**
     * Server-side streaming - bytes
     */
    File serverStreamBytes();
}
