package com.mentit.mento.domain.comment.service;

import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.board.repository.BoardRepository;
import com.mentit.mento.domain.comment.dto.CommentCreateRequest;
import com.mentit.mento.domain.comment.dto.CommentUpdateRequest;
import com.mentit.mento.domain.comment.dto.CommentsResponse;
import com.mentit.mento.domain.comment.entity.Comment;
import com.mentit.mento.domain.comment.repository.CommentRepository;
import com.mentit.mento.domain.users.entity.Users;
import com.mentit.mento.domain.users.repository.UserRepository;
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
    public void createComment(CustomUserDetail customUserDetail, Long boardId, CommentCreateRequest commentCreateRequest) {
        Users findUserByUserDetail = getUser(customUserDetail);

        if (!findUserByUserDetail.getNickname().equals(commentCreateRequest.getWriter())) {
            throw new MemberException(ExceptionCode.NOT_MATCHED_WRITER);
        }

        Board findBoardById = getBoard(boardId);

        Comment createdComment = Comment.builder()
                .writer(findUserByUserDetail)
                .board(findBoardById)
                .comment(commentCreateRequest.getContent())
                .build();

        findBoardById.getComments().add(createdComment);

        boardRepository.save(findBoardById);

        commentRepository.save(createdComment);

    }

    @Transactional
    public void updateComment(CustomUserDetail customUserDetail,Long commentId, CommentUpdateRequest commentUpdateRequest) {
        Users findUserByUserDetail = getUser(customUserDetail);

        if (!findUserByUserDetail.getNickname().equals(commentUpdateRequest.getWriter())) {
            throw new MemberException(ExceptionCode.NOT_MATCHED_WRITER);
        }

        Comment findCommentById = commentRepository.findById(commentId).orElseThrow(
                () -> new CommentException(ExceptionCode.NOT_FOUND_COMMENT)
        );

        Comment updatedComment = findCommentById.toBuilder()
                .comment(commentUpdateRequest.getContent())
                .build();

        commentRepository.save(updatedComment);
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
            Board board = getBoard(boardId); // 게시판 존재 여부 체크
            Page<Comment> comments = commentRepository.findAllByBoard(board.getBoardId(), pageable); // 댓글 조회

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
        Board board = getBoard(boardId);
         return commentRepository.countByBoard(board);
        }
    }

