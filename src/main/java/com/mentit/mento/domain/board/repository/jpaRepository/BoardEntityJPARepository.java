package com.mentit.mento.domain.board.repository.jpaRepository;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BoardEntityJPARepository extends JpaRepository<BoardEntity,Long> {
    Optional<BoardEntity> findByBoardId(Long boardId);

    Optional<List<BoardEntity>> findTop3ByOrderByCreatedAtDesc();

    List<BoardEntity> findByBoardIdIn(Set<Long> matchedBoardIds);

    Optional<List<BoardEntity>> findTop3ByOrderByViewCountDesc();
}
