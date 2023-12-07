package com.nemo.grpcexampleclient.controller;

import com.nemo.grpcexampleclient.service.BidirectionalStreamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author Mirkamol
 * @version 1.0
 * @date 2023/12/07
 */
@RestController
@Api(value = "Two-way streaming", tags = "Two-way streaming")
@RequestMapping("BidirectionalStream")
public class BidirectionalStreamController {

    @Autowired
    BidirectionalStreamService bidirectionalStreamService;

    @ApiOperation(value = "Two-way streaming - string")
    @PostMapping
    public String bidirectionalStreamString() {
        return bidirectionalStreamService.bidirectionalStreamString();
    }
}
