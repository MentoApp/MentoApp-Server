package com.mentit.mento.domain.comment.service;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.service.port.BoardRepository;
import com.mentit.mento.domain.comment.dto.request.CommentCreate;
import com.mentit.mento.domain.comment.dto.request.CommentUpdate;
import com.mentit.mento.domain.comment.dto.CommentsResponse;
import com.mentit.mento.domain.comment.entity.Comment;
import com.mentit.mento.domain.comment.entity.CommentEntity;
import com.mentit.mento.domain.comment.service.port.CommentRepository;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.service.port.UserRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.BoardException;
import com.mentit.mento.global.exception.customException.CommentException;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void createComment(CustomUserDetail customUserDetail, Long boardId, CommentCreate commentCreate) {
        Users findUserByUserDetail = getUser(customUserDetail);

        if (!findUserByUserDetail.getNickname().equals(commentCreate.getWriter())) {
            throw new MemberException(ExceptionCode.NOT_MATCHED_WRITER);
        }

        Board findBoardEntityById = getBoard(boardId);

        Comment createdCommentEntity = Comment.builder()
                .writer(findUserByUserDetail)
                .board(findBoardEntityById)
                .comment(commentCreate.getContent())
                .build();

        findBoardEntityById.getComments().add(createdCommentEntity);

        boardRepository.save(findBoardEntityById);

        commentRepository.save(createdCommentEntity);

    }

    @Transactional
    public void updateComment(CustomUserDetail customUserDetail,Long commentId, CommentUpdate commentUpdate) {
        Users findUserByUserDetail = getUser(customUserDetail);

        if (!findUserByUserDetail.getNickname().equals(commentUpdate.getWriter())) {
            throw new MemberException(ExceptionCode.NOT_MATCHED_WRITER);
        }

        Comment findComment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommentException(ExceptionCode.NOT_FOUND_COMMENT)
        );

        Comment updatedCommentEntity = findComment.toBuilder()
                .comment(commentUpdate.getContent())
                .build();

        commentRepository.save(updatedCommentEntity);
    }

    private Board getBoard(Long boardId) {
        return boardRepository.findByBoardId(boardId).orElseThrow(
                ()-> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
        );
    }

    private Users getUser(CustomUserDetail customUserDetail) {
        return userRepository.findById(customUserDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }

    public void deleteComment(CustomUserDetail customUserDetail, Long commentId) {
        Users findUserByUserDetail = getUser(customUserDetail);
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommentException(ExceptionCode.NOT_FOUND_COMMENT)
        );
        if(!comment.getWriter().getNickname().equals(findUserByUserDetail.getNickname())) {
            throw new MemberException(ExceptionCode.NOT_MATCHED_WRITER);
        }
        commentRepository.delete(comment);
    }

        @Transactional(readOnly = true)
        public Page<CommentsResponse> getCommentsByBoardId(Long boardId, Pageable pageable) {
            Board boardEntity = getBoard(boardId); // 게시판 존재 여부 체크
            Page<Comment> comments = commentRepository.findAllByBoard(boardEntity.getBoardId(), pageable); // 댓글 조회

            // CommentsResponse 변환
            List<CommentsResponse> commentsResponses = comments.stream()
                    .map(comment -> {
                        CommentsResponse response = new CommentsResponse();
                        response.setWriter(comment.getWriter().getNickname()); // 작성자 이름
                        response.setComment(comment.getComment()); // 댓글 내용
                        response.setWriteDate(comment.getCreatedAt()); // 작성 날짜
                        return response;
                    })
                    .collect(Collectors.toList());

            return new PageImpl<>(commentsResponses, pageable, comments.getTotalElements());
        }

        public Long getCommentCount(Long boardId) {
        Board boardEntity = getBoard(boardId);
         return commentRepository.countByBoard(boardEntity);
        }
    }

