package com.nemo.grpcexampleclient.controller;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import com.nemo.grpcexampleclient.service.ServerSideStreamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author Mirkamol
 * @version 1.0
 * @date 2023/12/07
 */
@Slf4j
@RestController
@Api(value = "Server-side streaming", tags = "Server-side streaming")
@RequestMapping("ServerSideStream")
public class ServerSideStreamController {

    @Autowired
    private ServerSideStreamService serverSideStreamService;

    @PostMapping("serverStreamString")
    @ApiOperation(value = "Server-side streaming - string")
    public String serverStreamString() {
        return serverSideStreamService.serverStreamString();
    }

    @GetMapping("serverStreamBytes")
    @ApiOperation(value = "Server-side streaming - bytes")
    public void serverStreamBytes(HttpServletResponse response) {
        File file = serverSideStreamService.serverStreamBytes();
        String fileType = FileTypeUtil.getType(file);

        // step1. Configure response
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
        response.setContentType("application/octet-stream");
        try {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="
                    + URLEncoder.encode("Server-side streaming download files." + fileType, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // step2. Implement file download
        byte[] buffer = new byte[1024];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             ServletOutputStream os = response.getOutputStream()) {
            int length;
            while ((length = bis.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            log.info("Server streaming interface file download successful");
        } catch (Exception e) {
            log.info("Server streaming interface file download failed");
            e.printStackTrace();
        } finally {
            boolean del = FileUtil.del(file);
            log.info("Delete buffer files：" + del);
        }
    }
}
