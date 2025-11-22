package com.example.chilink.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    /**
     * RedisConnectionFactory
     * - Lettuce 기반의 커넥션 팩토리 생성
     * - 다른 Redis 관련 빈들이 이 팩토리를 통해 Redis에 접속함
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /**
     * RedisTemplate 설정
     * - 키는 문자열, 값은 JSON 직렬화 방식으로 설정
     * - Device 객체(또는 기타 DTO)를 publish/subscribe 할 때 사용
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // 키는 문자열로 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 값은 JSON (Jackson) 직렬화
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    /**
     * 토픽 빈
     * - 여러 곳에서 동일 토픽명을 주입받아 사용하도록 중앙에서 관리
     * - 예: "device-events" 채널로 모든 Device 이벤트를 보냄
     */
    @Bean
    public ChannelTopic deviceTopic() {
        return new ChannelTopic("device-events");
    }

    /**
     * RedisMessageListenerContainer
     * - Redis의 Pub/Sub 메시지 수신을 담당
     * - 실제 리스너(Subscriber)는 나중에 container.addMessageListener(...)로 등록함
     */
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
