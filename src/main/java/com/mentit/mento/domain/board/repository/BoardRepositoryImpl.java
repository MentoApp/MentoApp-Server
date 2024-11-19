package com.mentit.mento.domain.board.repository;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.repository.jpaRepository.BoardEntityJPARepository;
import com.mentit.mento.domain.board.service.port.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepository {
    private final BoardEntityJPARepository boardJPARepository;

    @Override
    public Board save(Board createdBoard) {
        return boardJPARepository.save(BoardEntity.from(createdBoard)).to();
    }

    @Override
    public Optional<Board> findByBoardId(Long boardId) {
        return boardJPARepository.findByBoardId(boardId).map(BoardEntity::to);
    }

    @Override
    public void delete(Board findBoard) {
        boardJPARepository.delete(BoardEntity.from(findBoard));
    }

    @Override
    public Optional<Board> findById(Long boardId) {
        return boardJPARepository.findById(boardId).map(BoardEntity::to);
    }

    @Override
    public Optional<List<Board>> findTop3ByOrderByViewCountDesc() {
        return boardJPARepository.findTop3ByOrderByViewCountDesc().map(boardEntities -> boardEntities.stream().map(BoardEntity::to).toList());
    }

    @Override
    public List<Board> findAll() {
        return boardJPARepository.findAll().stream().map(BoardEntity::to).toList();
    }
}
