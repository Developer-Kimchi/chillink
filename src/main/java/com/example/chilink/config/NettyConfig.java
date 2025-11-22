package com.example.chilink.config;


import com.example.chilink.infrastructure.netty.NettyTcpServer;
import com.example.chilink.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class NettyConfig {

    private final NettyTcpServer nettyTcpServer;

    @Value("${netty.port}")
    private int port;

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try {
                nettyTcpServer.start(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
