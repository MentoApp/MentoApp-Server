package com.mentit.mento.domain.boardLike.repository;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.boardLike.domain.BoardLikeEntity;
import com.mentit.mento.domain.boardLike.service.port.BoardLikeRepository;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardLikeRepositoryImpl implements BoardLikeRepository {
    private final BoardLikeJPARepository boardLikeJPARepository;

    @Override
    public long countByBoard(BoardEntity boardEntity) {
        return boardLikeJPARepository.countByBoardEntity(boardEntity);
    }

    @Override
    public Optional<BoardLikeEntity> findBoardLikeByBoardAndUsersEntity(BoardEntity findBoardByBoardId, UsersEntity from) {
        return boardLikeJPARepository.findBoardLikeByBoardEntityAndUser(findBoardByBoardId, from);
    }

    @Override
    public BoardLikeEntity save(BoardLikeEntity updatedBoardLike) {
        return boardLikeJPARepository.save(updatedBoardLike);
    }

    @Override
    public void delete(BoardLikeEntity findBoardLikeEntity) {
        boardLikeJPARepository.delete(findBoardLikeEntity);
    }
}
