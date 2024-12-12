package com.mentit.mento.domain.board.service.port;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.domain.entity.SavedBoardEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedBoardRepository {
    SavedBoardEntity save(SavedBoardEntity savedBoardEntity);

    void deleteByBoardAndUser(BoardEntity boardEntity, UsersEntity usersEntity);

    List<SavedBoardEntity> findAllByUsers(UsersEntity usersEntity);

    void flush();

}
