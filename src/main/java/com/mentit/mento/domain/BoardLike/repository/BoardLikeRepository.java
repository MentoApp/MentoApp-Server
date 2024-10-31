package com.mentit.mento.domain.BoardLike.repository;

import com.mentit.mento.domain.BoardLike.entity.BoardLike;
import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike,Long> {

    Optional<BoardLike> findBoardLikeByBoardAndUser(Board board, Users user);

    long countByBoard(Board board);
}
