package com.mentit.mento.domain.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentsResponse {

    private String writer;
    private String comment;
    private LocalDateTime writeDate;
}
