package com.mentit.mento.domain.board.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FindSimilarBoardResponse {

    private String title;

    private String writer;

    private String content;

    private LocalDateTime createdTime;

    private List<String> boardKeywords;

    private UserInfoInBoardResponse userInfo;

    private String imageList;

    private Long viewCount;

    private Long likeCount;

    private Long commentCount;
}
