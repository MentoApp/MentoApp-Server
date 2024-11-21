package com.mentit.mento.domain.BoardLike.service.port;

import com.mentit.mento.domain.BoardLike.domain.BoardLike;
import com.mentit.mento.domain.BoardLike.domain.BoardLikeEntity;
import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;

import java.util.Optional;

public interface BoardLikeRepository {
    long countByBoard(BoardEntity board);

    Optional<BoardLikeEntity> findBoardLikeByBoardAndUsersEntity(BoardEntity findBoardByBoardEntityId, UsersEntity from);

    BoardLikeEntity save(BoardLikeEntity updatedBoardLike);
}
