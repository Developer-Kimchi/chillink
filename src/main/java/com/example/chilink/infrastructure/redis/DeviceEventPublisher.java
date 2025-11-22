package com.example.chilink.infrastructure.redis;

import com.example.chilink.domain.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeviceEventPublisher {

    // Redis로 publish 하기 위한 템플릿 (String key, Object value)
    private final RedisTemplate<String, Object> redisTemplate;

    // 발행할 토픽 (RedisConfig에서 정의한 deviceTopic)
    private final ChannelTopic deviceTopic;

    /**
     * Device 객체를 Redis의 토픽으로 발행
     * - Device를 그대로 직렬화하여 전송 (GenericJackson2JsonRedisSerializer 사용)
     * - 다른 프로세스(또는 같은 앱의 다른 인스턴스)가 구독하고 있으면 수신됨
     */
    public void publish(Device device) {
        // 토픽명과 메시지(여기서는 device 객체)를 전달
        redisTemplate.convertAndSend(deviceTopic.getTopic(), device);
    }
}
