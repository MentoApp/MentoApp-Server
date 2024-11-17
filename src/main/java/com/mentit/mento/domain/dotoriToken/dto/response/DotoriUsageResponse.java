package com.mentit.mento.domain.dotoriToken.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class DotoriUsageResponse {

    private List<DotoriGiveResponse> dotoriGiveResponseList;
    private List<DotoriEarnRseponse> dotoriEarnRseponseList;



}
