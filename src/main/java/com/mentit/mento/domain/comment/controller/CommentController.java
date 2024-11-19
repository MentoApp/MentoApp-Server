package com.mentit.mento.domain.comment.controller;

import com.mentit.mento.domain.comment.dto.request.CommentCreate;
import com.mentit.mento.domain.comment.dto.request.CommentUpdate;
import com.mentit.mento.domain.comment.dto.CommentsResponse;
import com.mentit.mento.domain.comment.service.CommentService;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 달기
    @PostMapping("/{boardId}")
    public Response<Void> createComment(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long boardId,
            CommentCreate commentCreate
    ) {
        commentService.createComment(customUserDetail, boardId, commentCreate);

        return Response.success(HttpStatus.OK, "댓글 작성 성공");
    }

    //댓글 수정
    @PatchMapping("/{commentId}")
    public Response<Void> updateComment(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long commentId,
            CommentUpdate commentUpdate
    ) {
        commentService.updateComment(customUserDetail, commentId, commentUpdate);

        return Response.success(HttpStatus.OK, "댓글 수정 성공");
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public Response<Void> deleteComment(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(customUserDetail, commentId);

        return Response.success(HttpStatus.OK, "댓글 삭제 성공");
    }

    //댓글 조회
    @GetMapping("/{boardId}")
    public Response<Page<CommentsResponse>> getComment(
            @PathVariable Long boardId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "3") int size) {
    {
            Pageable pageable = PageRequest.of(page, size);
            Page<CommentsResponse> comments = commentService.getCommentsByBoardId(boardId, pageable);

            return Response.success(HttpStatus.OK, "댓글 리스트 반환", comments);
        }
    }
}
