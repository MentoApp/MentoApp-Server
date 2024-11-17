package com.mentit.mento.domain.users.infrastructure;

import com.mentit.mento.domain.users.domain.BoardKeyword;
import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.jpaRepository.BoardKeywordJPARepository;
import com.mentit.mento.domain.users.service.port.BoardKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardKeywordRepositoryImpl implements BoardKeywordRepository {

    private final BoardKeywordJPARepository boardKeywordJPARepository;

    public void deleteAllByUsers(UsersEntity findUserByUserDetail) {
        boardKeywordJPARepository.deleteAllByUsers(findUserByUserDetail);
    }

    @Override
    public BoardKeyword save(BoardKeyword boardKeyword) {
        return boardKeywordJPARepository.save(BoardKeywordEntity.from(boardKeyword)).toModel();
    }
}
