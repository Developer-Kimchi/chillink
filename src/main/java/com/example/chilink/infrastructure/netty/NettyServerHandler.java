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

    /**
     * 클라이언트로부터 메시지 수신 시 호출
     * 메시지 포맷: "DEVICE_ID:COMMAND" 예: "1:ON", "2:OFF", "3:TOGGLE"
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {

        System.out.println("Received from device: " + msg);

        try {
            // 메시지 파싱
            String[] parts = msg.split(":");
            Long deviceId = Long.parseLong(parts[0]); // 장치 ID
            String command = parts[1].toUpperCase().trim();  // 명령어 (ON/OFF/TOGGLE)

            Device updatedDevice;

            // 명령어에 따른 상태 변경
            switch (command) {
                case "ON":
                    updatedDevice = deviceService.setDeviceStatus(deviceId, true);
                    break;
                case "OFF":
                    updatedDevice = deviceService.setDeviceStatus(deviceId, false);
                    break;
                case "TOGGLE":
                    updatedDevice = deviceService.toggleDeviceStatus(deviceId);
                    break;
                default:
                    ctx.writeAndFlush("Unknown command\n");
                    return;
            }

            // 상태 변경 후 클라이언트에 응답 전송
            ctx.writeAndFlush("Device " + updatedDevice.getId() +
                    " status: " + updatedDevice.getStatus() + "\n");

        } catch (Exception e) {
            // 오류 발생 시 클라이언트에 메시지 전송
            ctx.writeAndFlush("Error: " + e.getMessage() + "\n");
        }
    }

    /**
     * 예외 발생 시 호출
     * 소켓 연결 종료
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
