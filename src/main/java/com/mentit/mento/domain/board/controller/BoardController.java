package com.mentit.mento.domain.board.controller;

import com.mentit.mento.domain.board.dto.BoardCreateRequest;
import com.mentit.mento.domain.board.dto.BoardUpdateRequest;
import com.mentit.mento.domain.board.dto.FindBoardResponse;
import com.mentit.mento.domain.board.dto.FindSimilarBoardResponse;
import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.board.service.BoardService;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //생성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Void> createBoard(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestPart BoardCreateRequest boardCreateRequest,
            @RequestPart(required = false) List<MultipartFile> images
            ){
        boardService.createBoard(customUserDetail,boardCreateRequest,images);

        return Response.success(HttpStatus.OK,"게시판 작성 성공");
    }
    //수정
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<Void> updateBoard(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestPart BoardUpdateRequest boardUpdateRequest,
            @RequestPart(required = false) List<MultipartFile> images
    ){
        boardService.updateBoard(customUserDetail,boardUpdateRequest,images);

        return Response.success(HttpStatus.OK,"게시판 수정 성공");

    }
    //삭제
    @DeleteMapping("/{boardId}")
    public Response<Void> deleteBoard(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long boardId
    ){
        boardService.deleteBoard(customUserDetail, boardId);

        return Response.success(HttpStatus.OK, "게시판 삭제 성공");
    }
    //조회
    @GetMapping("/{boardId}")
    public Response<FindBoardResponse> getBoard(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
        @PathVariable Long boardId
    ){
        FindBoardResponse findBoardResponse= boardService.findBoard(customUserDetail,boardId);

        return Response.success(HttpStatus.OK, "게시판 조회 성공",findBoardResponse);
    }

    //키워드 포함 게시글 반환하기(3개)
    @GetMapping("/similar-board")
    public Response<List<FindSimilarBoardResponse>> findBoardsContainsKeywords(
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ){
        List<FindSimilarBoardResponse> findBoardResponses = boardService.findBoardContainsKeywords(customUserDetail);
        return Response.success(HttpStatus.OK,"키워드 유사 게시글 조회 성공",findBoardResponses);

    }

}
