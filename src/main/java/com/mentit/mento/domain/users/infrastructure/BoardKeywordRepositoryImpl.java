package com.mentit.mento.domain.users.infrastructure;

import com.mentit.mento.domain.users.domain.BoardKeyword;
import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.jpaRepository.BoardKeywordJPARepository;
import com.mentit.mento.domain.users.service.port.BoardKeywordRepository;
import com.mentit.mento.domain.users.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardKeywordRepositoryImpl implements BoardKeywordRepository {

    private final BoardKeywordJPARepository boardKeywordJPARepository;
    private final UserRepositoryImpl userRepositoryImpl;

    public void deleteAllByUsers(UsersEntity findUserByUserDetail) {
        boardKeywordJPARepository.deleteAllByUsersEntity(findUserByUserDetail);
    }

    @Override
    public BoardKeyword save(BoardKeyword boardKeyword) {
        return boardKeywordJPARepository.save(BoardKeywordEntity.from(boardKeyword)).toModel();
    }
}
