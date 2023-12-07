package com.nemo.grpcexampleclient.controller;

import com.nemo.grpcexampleclient.service.ClientSideStreamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Mirkamol
 * @version 1.0
 * @date 2023/12/07
 */
@RestController
@Api(value = "client streaming", tags = "client streaming")
@RequestMapping("ClientSideStream")
public class ClientSideStreamController {

    @Autowired
    ClientSideStreamService clientSideStreamService;

    @PostMapping("clientStreamString")
    @ApiOperation(value = "client streaming - string")
    public String clientStreamString() {
        return clientSideStreamService.clientStreamString();
    }

    @PostMapping("clientStreamBytes")
    @ApiOperation(value = "client streaming - bytes")
    public String clientStreamBytes(MultipartFile file) {
        return clientSideStreamService.clientStreamBytes(file);
    }

    @PostMapping("clientStreamBytesByte")
    @ApiOperation(value = "client streaming - bytes The server receives through byte array")
    public String clientStreamBytesByte(MultipartFile file) {
        return clientSideStreamService.clientStreamBytesByte(file);
    }
}
