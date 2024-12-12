package com.mentit.mento.domain.board.domain.dto.request;

import com.mentit.mento.domain.board.constant.BoardKeywordForCreatingEnum;
import com.mentit.mento.domain.board.constant.BoardTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CreateBoard {
    @Schema(description = "제목", example = "게시글 예시1")
    private String title;
    @Schema(description = "작성자(유저 닉네임)")
    private String writer;
    @Schema(description = "게시글 내용", example = "안녕하세요 게시글입니다. 안녕히 계세요")
    private String content;
    @Schema(description = "게시글 키워드", example = "[\"프론트엔드 개발\", \"취업/이직\"]")
    private List<BoardKeywordForCreatingEnum> keywords;
    @Schema(description = "게시글 유형" , example = "IT 일상")
    private BoardTypeEnum boardTypeEnum;
}
