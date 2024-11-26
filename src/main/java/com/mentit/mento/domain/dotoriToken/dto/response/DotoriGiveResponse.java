package com.mentit.mento.domain.dotoriToken.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DotoriGiveResponse {
    private LocalDateTime timestamp;
    private String tradeType;
    private Long receiverId;
    private String message;
    private String boardTitle;
    private Long boardId;
    private String usageCount;
}
