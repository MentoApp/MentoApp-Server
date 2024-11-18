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
    public BoardFiles save(BoardFiles createdFile) {
        return boardFileJPARepository.save(BoardFilesEntity.from(createdFile)).to();
    }

    @Override
    public void deleteAllByBoard(Board findBoard) {
        boardFileJPARepository.deleteAllByBoard(BoardEntity.from(findBoard));
    }

    @Override
    public List<BoardFiles> findAllByBoard(Board findBoard) {
        return boardFileJPARepository.findAllByBoard(BoardEntity.from(findBoard)).stream().map(BoardFilesEntity::to).toList();
    }
}
