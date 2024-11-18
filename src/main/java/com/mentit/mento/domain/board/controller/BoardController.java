package com.mentit.mento.domain.board.controller;

import com.mentit.mento.domain.board.domain.dto.request.BoardCreate;
import com.mentit.mento.domain.board.domain.dto.request.BoardUpdate;
import com.mentit.mento.domain.board.domain.dto.response.FindBoardResponse;
import com.mentit.mento.domain.board.domain.dto.response.FindSimilarBoardResponse;
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
    public Response<FindBoardResponse> createBoard(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestPart BoardCreate boardCreate,
            @RequestPart(required = false) List<MultipartFile> images
            ){
        FindBoardResponse findBoardResponse = boardService.createBoard(customUserDetail, boardCreate, images);

        return Response.success(HttpStatus.OK,"게시판 작성 성공",findBoardResponse);
    }
    //수정
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<FindBoardResponse> updateBoard(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestPart BoardUpdate boardUpdate,
            @RequestPart(required = false) List<MultipartFile> images
    ){
        FindBoardResponse findBoardResponse = boardService.updateBoard(customUserDetail, boardUpdate, images);

        return Response.success(HttpStatus.OK,"게시판 수정 성공",findBoardResponse);

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

    //인기있는 게시물 노출
    @GetMapping("/top3board")
    public Response<List<FindBoardResponse>> findTop3Boards(
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ){
        List<FindBoardResponse> findList = boardService.findTop3Boards(customUserDetail);

        return Response.success(HttpStatus.OK,"인기 게시글 조회 성공",findList);
    }

}
