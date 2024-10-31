package com.mentit.mento.domain.comment.repository;

import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteAllByBoard(Board findBoard);

    @EntityGraph(attributePaths = {"writer"})
    Optional<Comment> findById(Long id);

    @Query("SELECT c FROM Comment c WHERE c.board.boardId = :boardId")
    Page<Comment> findAllByBoard(Long boardId, Pageable pageable);

    Long countByBoard(Board board);
}
