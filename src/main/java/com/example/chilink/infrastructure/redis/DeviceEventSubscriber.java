package com.example.chilink.infrastructure.redis;

import com.example.chilink.domain.Device;
import com.example.chilink.infrastructure.netty.NettyTcpServer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeviceEventSubscriber implements MessageListener {

    // Netty 서버 인스턴스 - 수신된 이벤트를 TCP 클라이언트로 브로드캐스트할 때 사용
    private final NettyTcpServer nettyTcpServer;

    /**
     * Redis에서 메시지를 수신하면 호출됨
     * Message.getBody()는 바이너리 데이터(여기선 JSON 문자열)임.
     * - JSON 문자열로 변환한 후 NettyTcpServer.broadcast(...)로 전달
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 바이트를 문자열로 변환 (UTF-8 기본 가정)
        String json = new String(message.getBody());
        // 간단한 로그 출력 (운영에서는 로거 사용 권장)
        System.out.println("[DeviceEventSubscriber] Received: " + json);

        // Netty 서버에 브로드캐스트 (모든 연결된 TCP 클라이언트에게 전송)
        nettyTcpServer.broadcast("DEVICE_EVENT: " + json);
    }
}