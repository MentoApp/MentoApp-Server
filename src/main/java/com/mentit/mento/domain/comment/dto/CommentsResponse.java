package com.mentit.mento.domain.comment.dto;

import com.mentit.mento.domain.board.domain.dto.response.UserInfoInBoardResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentsResponse {

    private String writer;
    private String comment;
    private LocalDateTime writeDate;
    private UserInfoInBoardResponse userInfo;
}
