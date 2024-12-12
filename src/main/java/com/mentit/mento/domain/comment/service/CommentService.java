package com.mentit.mento.domain.comment.service;

import com.mentit.mento.domain.board.domain.dto.response.UserInfoInBoardResponse;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.service.port.BoardRepository;
import com.mentit.mento.domain.comment.dto.CommentsResponse;
import com.mentit.mento.domain.comment.dto.request.CommentCreate;
import com.mentit.mento.domain.comment.dto.request.CommentUpdate;
import com.mentit.mento.domain.comment.entity.CommentEntity;
import com.mentit.mento.domain.comment.service.port.CommentRepository;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
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
        UsersEntity findUserByUserDetail = getUser(customUserDetail);

        if (!findUserByUserDetail.getNickname().equals(commentCreate.getWriter())) {
            throw new MemberException(ExceptionCode.NOT_MATCHED_WRITER );
        }

        BoardEntity boardEntity = getBoard(boardId);

        CommentEntity createdCommentEntity = CommentEntity.builder()
                .writer(findUserByUserDetail)
                .boardEntity(boardEntity)
                .comment(commentCreate.getContent())
                .build();

        boardEntity.getCommentEntities().add(createdCommentEntity);

        boardRepository.save(boardEntity);

        commentRepository.save(createdCommentEntity);

    }

    @Transactional
    public void updateComment(CustomUserDetail customUserDetail,Long commentId, CommentUpdate commentUpdate) {
        UsersEntity findUserByUserDetail = getUser(customUserDetail);

        if (!findUserByUserDetail.getNickname().equals(commentUpdate.getWriter())) {
            throw new MemberException(ExceptionCode.NOT_MATCHED_WRITER);
        }

        CommentEntity findComment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommentException(ExceptionCode.NOT_FOUND_COMMENT)
        );

        CommentEntity updatedCommentEntity = findComment.toBuilder()
                .comment(commentUpdate.getContent())
                .build();

        commentRepository.save(updatedCommentEntity);
    }

    private BoardEntity getBoard(Long boardId) {
        return boardRepository.findByBoardId(boardId).orElseThrow(
                ()-> new BoardException(ExceptionCode.NOT_FOUND_BOARD)
        );
    }

    private UsersEntity getUser(CustomUserDetail customUserDetail) {
        return userRepository.findById(customUserDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }

    public void deleteComment(CustomUserDetail customUserDetail, Long commentId) {
        UsersEntity findUserByUserDetail = getUser(customUserDetail);
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommentException(ExceptionCode.NOT_FOUND_COMMENT)
        );
        if(!comment.getWriter().getNickname().equals(findUserByUserDetail.getNickname())) {
            throw new MemberException(ExceptionCode.NOT_MATCHED_WRITER);
        }
        commentRepository.delete(comment);
    }

        @Transactional(readOnly = true)
        public Page<CommentsResponse> getCommentsByBoardId(Long boardId, Pageable pageable) {
            BoardEntity boardEntity = getBoard(boardId); // 게시판 존재 여부 체크
            Page<CommentEntity> comments = commentRepository.findAllByBoard(boardEntity.getBoardId(), pageable); // 댓글 조회

            // CommentsResponse 변환
            List<CommentsResponse> commentsResponses = comments.stream()
                    .map(comment -> {
                        UsersEntity writer = comment.getWriter();

                        UserInfoInBoardResponse UserInfo = UserInfoInBoardResponse.builder()
                                .userId(writer.getUserId())
                                .nickname(writer.getNickname())
                                .profileImage(writer.getProfileImage())
                                .build();

                        CommentsResponse response = new CommentsResponse();
                        response.setWriter(comment.getWriter().getNickname()); // 작성자 이름
                        response.setComment(comment.getComment()); // 댓글 내용
                        response.setWriteDate(comment.getCreatedAt()); // 작성 날짜
                        response.setUserInfo(UserInfo);
                        return response;
                    })
                    .collect(Collectors.toList());

            return new PageImpl<>(commentsResponses, pageable, comments.getTotalElements());
        }

        public Long getCommentCount(Long boardId) {
        BoardEntity boardEntity = getBoard(boardId);
         return commentRepository.countByBoard(boardEntity);
        }
    }

