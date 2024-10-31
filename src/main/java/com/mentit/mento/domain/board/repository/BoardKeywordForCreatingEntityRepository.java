package com.mentit.mento.domain.board.repository;

import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.board.entity.BoardKeywordForCreatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardKeywordForCreatingEntityRepository extends JpaRepository<BoardKeywordForCreatingEntity,Long> {
    void deleteAllByBoard(Board findBoard);
}
