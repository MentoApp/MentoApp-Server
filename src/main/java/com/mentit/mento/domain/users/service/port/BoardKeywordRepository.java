package com.mentit.mento.domain.users.service.port;

import com.mentit.mento.domain.users.domain.BoardKeyword;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardKeywordRepository {

    void deleteAllByUsers(UsersEntity user);

    BoardKeyword save(BoardKeyword boardKeyword);
}
