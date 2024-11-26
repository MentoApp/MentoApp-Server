package com.mentit.mento.domain.dotoriToken.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DotoriEarnRseponse {
    private LocalDateTime timestamp;
    private String tradeType;
    private Long senderId;
    private String message;
    private String boardTitle;
    private Long boardId;
    private String usageCount;
}
