package com.mentit.mento.domain.board.domain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FindMyBoardResponse {

    private Long boardId;

    private String title;

    private String writer;

    private String content;

    private Long viewCount;

    private Long likeCount;

    private LocalDateTime createdTime;

    private List<String> boardKeywords;

    private List<String> imageList;

    private Long commentCount;

}
