package com.mentit.mento.domain.dotoriToken.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class TokenGiftRequest {
    private Long present_id;
    private Long receiver_id;
    private int tradeAmount;

}
