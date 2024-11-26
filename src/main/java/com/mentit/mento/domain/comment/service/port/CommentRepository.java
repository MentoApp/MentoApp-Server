package com.mentit.mento.domain.comment.service.port;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.comment.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CommentRepository {
    CommentEntity save(CommentEntity createdCommentEntity);

    Optional<CommentEntity> findById(Long commentId);

    void delete(CommentEntity commentEntity);

    Page<CommentEntity> findAllByBoard(Long boardId, Pageable pageable);

    Long countByBoard(BoardEntity boardEntity);

    void deleteAllByBoard(BoardEntity boardEntity);
}
