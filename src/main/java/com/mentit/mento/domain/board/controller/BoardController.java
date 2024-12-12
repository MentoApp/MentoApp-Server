package com.mentit.mento.domain.board.controller;

import com.mentit.mento.domain.board.domain.dto.request.BoardUpdate;
import com.mentit.mento.domain.board.domain.dto.request.CreateBoard;
import com.mentit.mento.domain.board.domain.dto.response.FindBoardResponse;
import com.mentit.mento.domain.board.domain.dto.response.FindMyBoardResponse;
import com.mentit.mento.domain.board.domain.dto.response.FindSimilarBoardResponse;
import com.mentit.mento.domain.board.service.BoardService;
import com.mentit.mento.global.response.Response;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Operation(summary = "게시판 작성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<FindBoardResponse> createBoard(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestPart CreateBoard createBoard,
            @RequestPart(required = false) List<MultipartFile> images
            ){
        FindBoardResponse findBoardResponse = boardService.createBoard(customUserDetail, createBoard, images);

        return Response.success(HttpStatus.OK,"게시판 작성 성공",findBoardResponse);
    }

    //수정
    @Operation(summary = "게시판 수정")
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
    @Operation(summary = "게시판 삭제")
    @DeleteMapping("/boardId")
    public Response<Void> deleteBoard(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestParam Long boardId
    ){
        boardService.deleteBoard(customUserDetail, boardId);

        return Response.success(HttpStatus.OK, "게시판 삭제 성공");
    }
    //단일 조회
    @Operation(summary = "게시판 단일 조회")
    @GetMapping("/boardId")
    public Response<FindBoardResponse> getBoard(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestParam Long boardId
    ){
        FindBoardResponse findBoardResponse= boardService.findOneBoard(boardId);

        return Response.success(HttpStatus.OK, "게시판 조회 성공",findBoardResponse);
    }

    //키워드 포함 게시글 반환하기(3개)
    @Operation(summary = "유사 키워드 게시글 3개 반환")
    @GetMapping("/similar-board")
    public Response<List<FindSimilarBoardResponse>> findBoardsContainsKeywords(
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ){
        List<FindSimilarBoardResponse> findBoardResponses = boardService.findBoardContainsKeywords(customUserDetail);
        return Response.success(HttpStatus.OK,"키워드 유사 게시글 조회 성공",findBoardResponses);
    }

    //인기있는 게시물 노출
    @Operation(summary = "조회수순 인기 게시물 3개 반환")
    @GetMapping("/top3board")
    public Response<List<FindBoardResponse>> findTop3Boards(
            @AuthenticationPrincipal CustomUserDetail customUserDetail
    ){
        List<FindBoardResponse> findList = boardService.findTop3Boards(customUserDetail);

        return Response.success(HttpStatus.OK,"인기 게시글 조회 성공",findList);
    }

    //내가 쓴 글 조회
    @Operation(summary = "내가 쓴 글 조회")
    @GetMapping("/myBoard")
    public Response<Page<FindMyBoardResponse>> findMyBoards(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestParam(required = false,defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size
    ){
        Pageable pageable = PageRequest.of(page,size);
        Page<FindMyBoardResponse> findMyBoards = boardService.findMyBoards(customUserDetail,pageable);

        return Response.success(HttpStatus.OK, "내가 쓴글 페이지 반환",findMyBoards);
    }

    //저장된 글 가져오기
    @Operation(summary = "저장된 글 조회")
    @GetMapping("mySavedBoard")
    public Response<Page<FindBoardResponse>> findMySavedBoards(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestParam(required = false,defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size
    ){
        Pageable pageable = PageRequest.of(page,size);
        Page<FindBoardResponse> findBoardResponses = boardService.findMySavedBoards(customUserDetail,pageable);

        return Response.success(HttpStatus.OK,"내가 저장한 게시판 반환",findBoardResponses);
    }

    //저장하기
    @Operation(summary = "게시글 저장하기")
    @GetMapping("/saveBoard")
    public Response<Void> saveBoard(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestParam Long boardId
    ){
        boardService.saveBoard(customUserDetail,boardId);

        return Response.success(HttpStatus.OK, "저장 성공");
    }

    //저장 삭제하기
    @Operation(summary = "게시글 저장 취소")
    @DeleteMapping("/deleteSavedBoard")
    public Response<Void> deleteSavedBoard(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @RequestParam Long boardId
    ){
        boardService.deleteSavedBoard(customUserDetail,boardId);

        return Response.success(HttpStatus.OK, "삭제 성공");

    }

}
