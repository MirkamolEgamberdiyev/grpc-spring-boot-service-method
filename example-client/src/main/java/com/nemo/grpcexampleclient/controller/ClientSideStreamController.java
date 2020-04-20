package com.nemo.grpcexampleclient.controller;

import com.nemo.grpcexampleclient.service.ClientSideStreamService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Nemo
 * @version 1.0
 * @date 2020/4/16
 */
@RestController
@RequestMapping("ServerSideStream")
public class ClientSideStreamController {

    @Autowired
    ClientSideStreamService clientSideStreamService;

    @PostMapping("clientStreamString")
    @ApiOperation(value = "客户端流式传输 - 字符串")
    public String clientStreamString() {
        return clientSideStreamService.clientStreamString();
    }

    @PostMapping("clientStreamBytes")
    @ApiOperation(value = "客户端流式传输 - bytes")
    public String clientStreamBytes(MultipartFile file) {
        return clientSideStreamService.clientStreamBytes(file);
    }
}
