package com.mentit.mento.domain.BoardLike.repository;

import com.mentit.mento.domain.BoardLike.entity.BoardLike;
import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike,Long> {

    Optional<BoardLike> findBoardLikeByBoardAndUser(Board board, UsersEntity user);

    long countByBoard(Board board);
}
