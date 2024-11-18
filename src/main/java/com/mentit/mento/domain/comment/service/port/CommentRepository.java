package com.mentit.mento.domain.comment.service.port;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommentRepository {
    Comment save(Comment createdCommentEntity);

    Optional<Comment> findById(Long commentId);

    void delete(Comment comment);

    Page<Comment> findAllByBoard(Long boardId, Pageable pageable);

    Long countByBoard(Board boardEntity);
}
