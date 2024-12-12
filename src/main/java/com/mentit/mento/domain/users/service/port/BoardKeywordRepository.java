package com.mentit.mento.domain.users.service.port;

import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardKeywordRepository {

    void deleteAllByUsers(UsersEntity user);

    BoardKeywordEntity save(BoardKeywordEntity boardKeyword);

    List<BoardKeywordEntity> saveAll(List<BoardKeywordEntity> list);
}
