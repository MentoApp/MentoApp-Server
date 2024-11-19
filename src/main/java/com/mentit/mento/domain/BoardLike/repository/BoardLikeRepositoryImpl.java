package com.mentit.mento.domain.BoardLike.repository;

import com.mentit.mento.domain.BoardLike.domain.BoardLike;
import com.mentit.mento.domain.BoardLike.domain.BoardLikeEntity;
import com.mentit.mento.domain.BoardLike.service.port.BoardLikeRepository;
import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardLikeRepositoryImpl implements BoardLikeRepository {
    private final BoardLikeJPARepository boardLikeJPARepository;

    @Override
    public long countByBoard(Board board) {
        return boardLikeJPARepository.countByBoardEntity(BoardEntity.from(board));
    }

    @Override
    public Optional<BoardLike> findBoardLikeByBoardAndUsersEntity(Board findBoardByBoardId, Users from) {
        return boardLikeJPARepository.findBoardLikeByBoardEntityAndUser(BoardEntity.from(findBoardByBoardId), UsersEntity.from(from)).map(BoardLikeEntity::to);
    }

    @Override
    public BoardLike save(BoardLike updatedBoardLike) {
        return boardLikeJPARepository.save(BoardLikeEntity.from(updatedBoardLike)).to();
    }
}
