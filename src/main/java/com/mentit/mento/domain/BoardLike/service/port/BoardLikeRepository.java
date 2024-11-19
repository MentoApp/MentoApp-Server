package com.mentit.mento.domain.BoardLike.service.port;

import com.mentit.mento.domain.BoardLike.domain.BoardLike;
import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.users.domain.Users;

import java.util.Optional;

public interface BoardLikeRepository {
    long countByBoard(Board board);

    Optional<BoardLike> findBoardLikeByBoardAndUsersEntity(Board findBoardByBoardEntityId, Users from);

    BoardLike save(BoardLike updatedBoardLike);
}
