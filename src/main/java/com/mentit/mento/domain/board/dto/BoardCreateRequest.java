package com.mentit.mento.domain.board.dto;

import com.mentit.mento.domain.board.constant.BoardKeywordForCreating;
import com.mentit.mento.domain.board.constant.BoardType;
import lombok.Data;

import java.util.List;

@Data
public class BoardCreateRequest {
    private String title;
    private String writer;
    private String content;
    private List<BoardKeywordForCreating> keywords;
    private BoardType boardType;
}
