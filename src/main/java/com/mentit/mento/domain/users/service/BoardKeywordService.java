package com.mentit.mento.domain.users.service;

import com.mentit.mento.domain.users.constant.BoardKeywordEnum;
import com.mentit.mento.domain.users.domain.BoardKeyword;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.UserRepositoryImpl;
import com.mentit.mento.domain.users.service.port.BoardKeywordRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
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
    private final UserRepositoryImpl userRepositoryImpl;

    @Transactional
    public void createUserBoardKeyword(List<BoardKeywordEnum> boardKeywordEnums, UsersEntity usersEntity) {

        List<BoardKeywordEntity> list = boardKeywordEnums.stream().map(keyword -> {
            return BoardKeywordEntity.builder()
                    .boardKeywordEnum(keyword)
                    .usersEntity(usersEntity)
                    .build();
        }).toList();

        boardKeywordRepository.saveAll(list);
    }

    @Transactional
    public void deleteExistingBoardKeywords(UsersEntity usersEntity) {
        boardKeywordRepository.deleteAllByUsers(usersEntity);
    }

    public List<String> getBoardKeywords(UsersEntity findUserByUserDetail) {
        List<String> boardKeywordList = new ArrayList<>();

        findUserByUserDetail.getBoardKeywords().forEach(
                boardKeyword -> boardKeywordList.add(boardKeyword.getBoardKeywordEnum().getKoreanValue())
        );
        return boardKeywordList;
    }

}