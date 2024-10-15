package com.mentit.mento.domain.users.service;

import com.mentit.mento.domain.users.constant.BoardKeyword;
import com.mentit.mento.domain.users.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.entity.Users;
import com.mentit.mento.domain.users.repository.BoardKeywordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardKeywordService {

    private final BoardKeywordRepository boardKeywordRepository;

    @Transactional
    public void createUserBoardKeyword(List<BoardKeyword> boardKeywords, Users user) {
        boardKeywords.forEach(keyword -> {
            BoardKeywordEntity boardKeywordEntity = BoardKeywordEntity.builder()
                    .boardKeyword(keyword)
                    .users(user)
                    .build();
            BoardKeywordEntity savedBoardKeyWordEntity = boardKeywordRepository.save(boardKeywordEntity);
            user.getBoardKeywords().add(savedBoardKeyWordEntity);
        });
    }

    @Transactional
    public void deleteExistingBoardKeywords(Users user) {
        boardKeywordRepository.deleteAllByUsers(user);
    }

    public List<String> getBoardKeywords(Users findUserByUserDetail) {
        List<String> boardKeywordList = new ArrayList<>();

        findUserByUserDetail.getBoardKeywords().forEach(
                boardKeyword -> {
                    boardKeywordList.add(boardKeyword.getBoardKeyword().getKoreanValue());
                }
        );
        return boardKeywordList;
    }

}