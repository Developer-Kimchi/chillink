package com.example.chilink.infrastructure.netty;

import com.example.chilink.service.DeviceService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.RequiredArgsConstructor;

/**
 * 클라이언트 소켓 채널 초기화 클래스
 * - 인코더/디코더 추가
 * - Device 이벤트 처리 핸들러 추가
 */
@RequiredArgsConstructor
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final DeviceService deviceService;

    @Override
    protected void initChannel(SocketChannel ch) {
        // 파이프라인에 문자열 디코더/인코더 추가
        ch.pipeline().addLast(new StringDecoder()); // ByteBuf → String
        ch.pipeline().addLast(new StringEncoder()); // String → ByteBuf

        // Device 이벤트 처리 핸들러 추가
        ch.pipeline().addLast(new NettyServerHandler(deviceService));
    }
}