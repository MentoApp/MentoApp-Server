package com.mentit.mento.domain.board.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoInBoardResponse {

    private Long userId;
    private String nickname;
    private String profileImage;
    private List<String> keyword;
    private String simpleIntroduce;


}
