package com.mentit.mento.domain.board.service.port;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    Board save(Board createdBoard);

    Optional<Board> findByBoardId(Long boardId);

    void delete(Board findBoard);

    Optional<Board> findById(Long boardId);

    Optional<List<Board>> findTop3ByOrderByViewCountDesc();

    List<Board> findAll();
}
