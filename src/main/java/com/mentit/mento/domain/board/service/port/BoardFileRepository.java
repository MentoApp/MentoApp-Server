package com.mentit.mento.domain.board.service.port;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.domain.entity.BoardFilesEntity;

import java.util.List;

public interface BoardFileRepository {
    BoardFilesEntity save(BoardFilesEntity createdFile);

    void deleteAllByBoardEntity(BoardEntity boardEntity);

    List<BoardFilesEntity> findAllByBoardEntity(BoardEntity boardEntity);
}
