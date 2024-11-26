package com.mentit.mento.domain.boardLike.controller;

import com.mentit.mento.domain.boardLike.service.BoardLikeService;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/boardLike")
@RequiredArgsConstructor
@Slf4j
public class BoardLikeController {

    private final BoardLikeService boardLikeService;

    @GetMapping("/{boardId}")
    public Response<Long> like(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long boardId
    ) {
        Long likeCount = boardLikeService.like(customUserDetail, boardId);
        log.info("count={}",likeCount);

        return Response.success(HttpStatus.OK,"좋아요/좋아요취소 요청 성공",likeCount);
    }



}
