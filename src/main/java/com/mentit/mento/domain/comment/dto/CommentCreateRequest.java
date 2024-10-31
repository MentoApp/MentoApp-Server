package com.mentit.mento.domain.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentCreateRequest {
    private String writer;
    private String content;
}
