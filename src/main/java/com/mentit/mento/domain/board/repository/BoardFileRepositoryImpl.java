package com.mentit.mento.domain.board.repository;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.BoardFiles;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.domain.entity.BoardFilesEntity;
import com.mentit.mento.domain.board.repository.jpaRepository.BoardFileJPARepository;
import com.mentit.mento.domain.board.service.port.BoardFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardFileRepositoryImpl implements BoardFileRepository {
    private final BoardFileJPARepository boardFileJPARepository;

    @Override
    public BoardFilesEntity save(BoardFilesEntity createdFile) {
        return boardFileJPARepository.save(createdFile);
    }

    @Override
    public void deleteAllByBoardEntity(BoardEntity boardEntity) {
        boardFileJPARepository.deleteAllByBoardEntity(boardEntity);
    }

    @Override
    public List<BoardFilesEntity> findAllByBoardEntity(BoardEntity boardEntity) {
        return boardFileJPARepository.findAllByBoardEntity(boardEntity);
    }
}
