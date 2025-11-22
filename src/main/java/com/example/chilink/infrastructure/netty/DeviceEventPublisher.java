package com.example.chilink.infrastructure.netty;

import com.example.chilink.domain.Device;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class DeviceEventPublisher {

    // 멀티스레드 환경에서 안전하게 사용할 수 있는 Set
    // 읽기는 자유롭게, 쓰기(add/remove) 시 내부 배열을 복사
    private final Set<NettyTcpServer> subscribers = new CopyOnWriteArraySet<>();

    // NettyTcpServer 등록
    public void subscribe(NettyTcpServer server) {
        subscribers.add(server);
    }

    // NettyTcpServer 해제
    public void unsubscribe(NettyTcpServer server) {
        subscribers.remove(server);
    }

    // Device 상태 변경 시 이벤트 발행
    public void publish(Device device) {
        String msg = "EVENT:" + device.getId() + ":" + device.getStatus();
        subscribers.forEach(server -> server.broadcast(msg));
    }
}
