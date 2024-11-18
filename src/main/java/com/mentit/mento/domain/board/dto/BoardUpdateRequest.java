package com.mentit.mento.domain.board.dto;

import com.mentit.mento.domain.board.constant.BoardKeywordForCreating;
import com.mentit.mento.domain.board.constant.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class BoardUpdateRequest {
    private Long boardId;
    @Schema(description = "제목", example = "게시글 수정 예시1")
    private String title;
    @Schema(description = "작성자(유저 닉네임)")
    private String writer;
    @Schema(description = "게시글 내용", example = "안녕하세요 수정한 게시글입니다. 반가워요")
    private String content;
    @Schema(description = "게시글 키워드", example = "[\"백엔드 개발\", \"트렌드\"]")
    private List<BoardKeywordForCreating> keywords;
    @Schema(description = "게시글 유형" , example = "아티클")
    private BoardType boardType;
}
