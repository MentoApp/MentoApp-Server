package com.mentit.mento.global.redis.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String,String> redisTemplate;

    public void saveAccessToken(String accessToken, Long id) {
        String key = "userId: " + id;

        redisTemplate.opsForValue().set(key, accessToken);
    }

    public String getAccessToken(String userId) {
        return redisTemplate.opsForValue().get("userId: " + userId);
    }
}
