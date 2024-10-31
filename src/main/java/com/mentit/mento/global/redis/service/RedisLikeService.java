package com.mentit.mento.global.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisLikeService {

    private final RedisTemplate<String,String> redisTemplate;

    public void setLikeCount(Long boardId, Long count) {
        redisTemplate.opsForValue().set("board_like_count:" + boardId, count.toString());
    }

    public void incrementLikeCount(Long boardId) {
        redisTemplate.opsForValue().increment("board_like_count:" + boardId, 1);
    }

    public Long getLikeCount(Long boardId) {
        String value = redisTemplate.opsForValue().get("board_like_count:" + boardId);
        return value != null ? Long.parseLong(value) : 0L;
    }

    public void decrementLikeCount(Long boardId) {
        redisTemplate.opsForValue().decrement("board_like_count:" + boardId);
    }
}
