package com.mentit.mento.domain.comment.repository;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.comment.entity.CommentEntity;
import com.mentit.mento.domain.comment.service.port.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
    private final CommentJPARepository commentJPARepository;

    @Override
    public CommentEntity save(CommentEntity comment) {
        return commentJPARepository.save(comment);
    }

    @Override
    public Optional<CommentEntity> findById(Long commentId) {
        return commentJPARepository.findByCommentId(commentId);
    }

    @Override
    public void delete(CommentEntity comment) {
        commentJPARepository.delete(comment);
    }

    @Override
    public Page<CommentEntity> findAllByBoard(Long boardId, Pageable pageable) {
        return commentJPARepository.findAllByBoard(boardId,pageable);
    }

    @Override
    public Long countByBoard(BoardEntity board) {
        return commentJPARepository.countByBoardEntity(board);
    }

    @Override
    public void deleteAllByBoard(BoardEntity board) {
        commentJPARepository.deleteAllByBoardEntity(board);
    }
}
