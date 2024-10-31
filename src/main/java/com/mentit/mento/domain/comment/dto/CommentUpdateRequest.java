package com.mentit.mento.domain.comment.dto;

import lombok.Data;

@Data
public class CommentUpdateRequest {
    private String writer;
    private String content;
}
