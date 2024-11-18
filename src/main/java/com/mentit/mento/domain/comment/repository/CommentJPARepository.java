package com.mentit.mento.domain.comment.repository;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.comment.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentJPARepository extends JpaRepository<CommentEntity, Long> {
    void deleteAllByBoardEntity(BoardEntity findBoardEntity);

    @EntityGraph(attributePaths = {"writer"})
    Optional<CommentEntity> findByCommentId(Long id);

    @Query("SELECT c FROM CommentEntity c WHERE c.boardEntity.boardId = :boardId")
    Page<CommentEntity> findAllByBoard(Long boardId, Pageable pageable);

    Long countByBoardEntity(BoardEntity boardEntity);
}
