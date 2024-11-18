package com.mentit.mento.domain.BoardLike.service.port;

import com.mentit.mento.domain.BoardLike.domain.BoardLike;
import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;

import java.util.Optional;

public interface BoardLikeRepository {
    long countByBoard(Board boardEntity);

    Optional<BoardLike> findBoardLikeByBoardAndUser(Board findBoardByBoardEntityId, UsersEntity from);

    BoardLike save(BoardLike updatedBoardLike);
}
