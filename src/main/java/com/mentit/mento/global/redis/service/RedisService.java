package com.mentit.mento.global.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String,String> redisTemplate;

    public void saveAccessToken(String accessToken, Long id) {
        System.out.println("수행됨");
        String key = "userId: " + id;
        System.out.println("id"+"="+id);

        redisTemplate.opsForValue().set(key, accessToken);
    }

    public String getAccessToken(String userId) {
        return redisTemplate.opsForValue().get("userId: " + userId);
    }
}
