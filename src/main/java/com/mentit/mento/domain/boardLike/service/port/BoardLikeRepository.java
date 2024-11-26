package com.mentit.mento.domain.boardLike.service.port;

import com.mentit.mento.domain.boardLike.domain.BoardLikeEntity;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;

import java.util.Optional;

public interface BoardLikeRepository {
    long countByBoard(BoardEntity board);

    Optional<BoardLikeEntity> findBoardLikeByBoardAndUsersEntity(BoardEntity findBoardByBoardEntityId, UsersEntity from);

    BoardLikeEntity save(BoardLikeEntity updatedBoardLike);

    void delete(BoardLikeEntity findBoardLikeEntity);
}
