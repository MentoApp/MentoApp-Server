package com.mentit.mento.domain.board.service.port;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.BoardFiles;

import java.util.List;

public interface BoardFileRepository {
    BoardFiles save(BoardFiles createdFile);

    void deleteAllByBoardEntity(Board findBoard);

    List<BoardFiles> findAllByBoardEntity(Board findBoard);
}
