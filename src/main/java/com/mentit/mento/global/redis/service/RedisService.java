package com.mentit.mento.global.redis.service;

import com.mentit.mento.domain.board.constant.BoardKeywordForCreatingEnum;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.global.helper.UserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String,String> redisTemplate;
    private final UserHelper userHelper;

    public void updateTokenStatus(UsersEntity usersEntity) {

        String key = "token-userId: " + usersEntity.getUserId();

        redisTemplate.opsForValue().set(key, String.valueOf(usersEntity.getTokenIssuedAt()));
    }

    public String getAccessToken(Long userId) {
        return redisTemplate.opsForValue().get("token-userId: " + userId);
    }

    // Save keywords for a specific board
    public void saveBoardKeywords(Long boardId, List<BoardKeywordForCreatingEnum> keywords) {
        String key = "boardKeywords:" + boardId;
        String[] keywordsArr = new String[keywords.size()];
        for (int i = 0; i < keywords.size(); i++) {
            keywordsArr[i] = keywords.get(i).toString();
        }
        redisTemplate.opsForSet().add(key, keywordsArr);
    }

    // Delete keywords for a specific board
    public void deleteBoardKeywords(Long boardId) {
        String key = "boardKeywords:" + boardId;
        redisTemplate.delete(key);
    }

    // Get keywords for a specific board
    public List<String> getBoardKeywords(Long boardId) {
        String key = "boardKeywords:" + boardId;
        return redisTemplate.opsForSet().members(key).stream().collect(Collectors.toList());
    }

    // Count matching keywords for a board
    public long countMatchingKeywords(Long boardId, List<String> keywords) {
        String key = "boardKeywords:" + boardId;
        return redisTemplate.opsForSet().intersect(key, keywords).size();
    }

}
