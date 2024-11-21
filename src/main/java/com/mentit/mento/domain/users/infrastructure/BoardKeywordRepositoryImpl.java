package com.mentit.mento.domain.users.infrastructure;

import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.jpaRepository.BoardKeywordJPARepository;
import com.mentit.mento.domain.users.service.port.BoardKeywordRepository;
import com.mentit.mento.domain.users.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardKeywordRepositoryImpl implements BoardKeywordRepository {

    private final BoardKeywordJPARepository boardKeywordJPARepository;
    private final UserRepository userRepository;

    public void deleteAllByUsers(UsersEntity findUserByUserDetail) {
        boardKeywordJPARepository.deleteAllByUsersEntity(findUserByUserDetail);
    }

    @Override
    public BoardKeywordEntity save(BoardKeywordEntity boardKeywordEntity) {
        return boardKeywordJPARepository.save(boardKeywordEntity);
    }

    @Override
    public List<BoardKeywordEntity> saveAll(List<BoardKeywordEntity> list) {
        return boardKeywordJPARepository.saveAll(list);
    }
}
