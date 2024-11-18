package com.mentit.mento.domain.comment.repository;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.comment.entity.Comment;
import com.mentit.mento.domain.comment.entity.CommentEntity;
import com.mentit.mento.domain.comment.service.port.CommentRepository;
import lombok.Getter;
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
    public Comment save(Comment comment) {
        return commentJPARepository.save(CommentEntity.from(comment)).to();
    }

    @Override
    public Optional<Comment> findById(Long commentId) {
        return commentJPARepository.findById(commentId).map(CommentEntity::to);
    }

    @Override
    public void delete(Comment comment) {
        commentJPARepository.delete(CommentEntity.from(comment));
    }

    @Override
    public Page<Comment> findAllByBoard(Long boardId, Pageable pageable) {
        return commentJPARepository.findAllByBoard(boardId,pageable).map(CommentEntity::to);
    }

    @Override
    public Long countByBoard(Board board) {
        return commentJPARepository.countByBoard(BoardEntity.from(board));
    }
}
