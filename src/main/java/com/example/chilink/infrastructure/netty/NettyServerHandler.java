package com.example.chilink.infrastructure.netty;

import com.example.chilink.domain.Device;
import com.example.chilink.service.DeviceService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Netty 서버에서 실제 클라이언트 메시지를 처리하는 핸들러
 * - 메시지 수신
 * - DeviceService 호출
 * - 상태 변경 후 클라이언트에게 응답 전송
 */
@RequiredArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {


    private final DeviceService deviceService;
    private final NettyTcpServer server;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        server.addChannel(ctx.channel());  // 채널 등록
        ctx.writeAndFlush("Welcome to Netty Server!\n");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        server.removeChannel(ctx.channel()); // 채널 제거
    }

    // "1:ON", "1:OFF", "1:TOGGLE" 형태로 split
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        if (msg == null || msg.trim().isEmpty()) return;

        System.out.println("================================== " + msg);

        String[] parts = msg.trim().split(":");
        if (parts.length != 2) {
            ctx.writeAndFlush("Invalid format\n");
            return;
        }

        Long deviceId = Long.valueOf(parts[0].trim());
        String command = parts[1].trim();

        // 명령어 처리
        switch (command) {
            case "ON":
                deviceService.setDeviceStatus(deviceId, true);
                ctx.writeAndFlush("Device " + deviceId + " turned ON\n");
                break;
            case "OFF":
                deviceService.setDeviceStatus(deviceId, false);
                ctx.writeAndFlush("Device " + deviceId + " turned OFF\n");
                break;
            case "TOGGLE":
                deviceService.toggleDeviceStatus(deviceId);
                ctx.writeAndFlush("Device " + deviceId + " toggled\n");
                break;
            default:
                ctx.writeAndFlush("Unknown command\n");
        }
    }
}
