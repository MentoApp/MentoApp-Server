package com.mentit.mento.domain.board.service.port;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.domain.entity.BoardKeywordForCreatingEntity;

public interface BoardKeywordForCreatingRepository {
    BoardKeywordForCreatingEntity save(BoardKeywordForCreatingEntity savedBoardKeyword);

    void deleteAllByBoard(BoardEntity findBoard);
}
