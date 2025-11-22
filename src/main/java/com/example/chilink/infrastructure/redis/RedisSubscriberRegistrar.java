package com.example.chilink.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class RedisSubscriberRegistrar {

    // RedisMessageListenerContainer: 리스너를 등록/관리하는 컨테이너
    private final RedisMessageListenerContainer listenerContainer;

    // 실제 메시지를 처리할 subscriber
    private final DeviceEventSubscriber subscriber;

    // 구독할 토픽
    private final ChannelTopic deviceTopic;

    /**
     * 애플리케이션 시작 후(빈 초기화 후) 호출되어
     * RedisMessageListenerContainer에 subscriber를 등록한다.
     */
    @PostConstruct
    public void init() {
        // deviceTopic 채널을 subscriber에 등록
        listenerContainer.addMessageListener(subscriber, deviceTopic);
    }
}
