package com.example.chilink.infrastructure.netty;

import com.example.chilink.service.DeviceService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * Netty TCP 서버 부트스트랩 클래스
 * Spring Bean으로 등록되어 DeviceService를 주입받아 이벤트 처리에 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettyTcpServer {

    private final DeviceService deviceService;

    @Value("${netty.port}")
    private int port;

    // 서버에 연결된 모든 클라이언트 채널 관리 (멀티스레드 안전)
// 이벤트 발생 시 모든 채널에 메시지 브로드캐스트
    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try {
                start(port);  // Netty 서버 시작
            } catch (InterruptedException e) {
                // 예외 처리: 로그 출력 등
                e.printStackTrace();
                Thread.currentThread().interrupt(); // 인터럽트 상태 유지
            }
        }).start();
    }


    /**
     * TCP 서버 시작 메서드
     *
     * @param port 서버가 리슨할 포트 번호
     * @throws InterruptedException
     */
    public void start(int port) throws InterruptedException {
        // 서버 소켓을 관리하는 Boss 그룹
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 클라이언트 연결 처리용 Worker 그룹
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup) // Boss/Worker 그룹 지정
                    .channel(NioServerSocketChannel.class) // NIO 기반 서버 채널
                    .childHandler(new NettyChannelInitializer(deviceService, this)) // 클라이언트 연결 시 초기화
                    .option(ChannelOption.SO_BACKLOG, 128) // 최대 대기 연결 수
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // Keep-Alive 활성화

            // 서버 바인딩 후 대기
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("Netty TCP Server started on port " + port);

            // 서버 종료 시까지 블로킹
            future.channel().closeFuture().sync();
        } finally {
            // 그룹 종료 시 자원 해제
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    // 새 클라이언트 연결 시 추가
    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    // 채널 종료 시 제거
    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    // 브로드캐스트
    public void broadcast(String message) {
        channels.forEach(ch -> ch.writeAndFlush(message + "\n"));
    }
}