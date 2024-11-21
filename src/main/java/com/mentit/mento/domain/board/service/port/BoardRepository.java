package com.mentit.mento.domain.board.service.port;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    BoardEntity save(BoardEntity createdBoard);

    Optional<BoardEntity> findByBoardId(Long boardId);

    void delete(BoardEntity findBoard);

    Optional<BoardEntity> findById(Long boardId);

    Optional<List<BoardEntity>> findTop3ByOrderByViewCountDesc();

    List<BoardEntity> findAll();
}
