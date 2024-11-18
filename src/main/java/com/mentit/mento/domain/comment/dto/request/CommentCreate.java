package com.mentit.mento.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CommentCreate {
    @Schema(description = "작성자")
    private String writer;
    @Schema(description = "작성 내용", example = "좋은 내용이네요!")
    private String content;
}
