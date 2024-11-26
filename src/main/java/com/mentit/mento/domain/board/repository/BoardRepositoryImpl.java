package com.mentit.mento.domain.board.repository;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.repository.jpaRepository.BoardEntityJPARepository;
import com.mentit.mento.domain.board.service.port.BoardRepository;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepository {
    private final BoardEntityJPARepository boardJPARepository;

    @Override
    public BoardEntity save(BoardEntity boardEntity) {
        return boardJPARepository.save(boardEntity);
    }

    @Override
    public Optional<BoardEntity> findByBoardId(Long boardId) {
        return boardJPARepository.findByBoardId(boardId);
    }

    @Override
    public void delete(BoardEntity findBoard) {
        boardJPARepository.delete(findBoard);
    }

    @Override
    public Optional<List<BoardEntity>> findTop3ByOrderByViewCountDesc() {
        return boardJPARepository.findTop3ByOrderByViewCountDesc();
    }

    @Override
    public List<BoardEntity> findAll() {
        return boardJPARepository.findAll();
    }

    @Override
    public Page<BoardEntity> findByUsers(UsersEntity usersEntity, Pageable pageable) {
        return boardJPARepository.findAllByWriter(usersEntity,pageable);
    }

    @Override
    public List<BoardEntity> findAllByUsers(Long usersId) {
        return boardJPARepository.findAllByUsers(usersId);
    }
}
