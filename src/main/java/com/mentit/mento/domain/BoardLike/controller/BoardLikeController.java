package com.mentit.mento.domain.BoardLike.controller;

import com.mentit.mento.domain.BoardLike.service.BoardLikeService;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/boardLike")
@RequiredArgsConstructor
public class BoardLikeController {

    private final BoardLikeService boardLikeService;

    @GetMapping("/like/{boardId}")
    public Response<Void> like(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long boardId
    ) {
        boardLikeService.like(customUserDetail,boardId);

        return Response.success(HttpStatus.OK,"좋아요 요청 성공");
    }



}
