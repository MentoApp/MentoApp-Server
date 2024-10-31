package com.mentit.mento.domain.board.repository;

import com.mentit.mento.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board,Long> {
    Optional<Board> findByBoardId(Long boardId);

    Optional<List<Board>> findTop3ByOrderByCreatedAtDesc();
}
