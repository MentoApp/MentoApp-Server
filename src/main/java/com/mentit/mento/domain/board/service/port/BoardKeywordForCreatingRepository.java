package com.mentit.mento.domain.board.service.port;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.BoardKeywordForCreating;

public interface BoardKeywordForCreatingRepository {
    BoardKeywordForCreating save(BoardKeywordForCreating savedBoardKeyword);

    void deleteAllByBoard(Board findBoard);
}
