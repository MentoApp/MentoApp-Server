package com.mentit.mento.domain.board.repository.jpaRepository;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.domain.entity.BoardKeywordForCreatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardKeywordForCreatingJPARepository extends JpaRepository<BoardKeywordForCreatingEntity,Long> {
    void deleteAllByBoard(BoardEntity findBoardEntity);
}
