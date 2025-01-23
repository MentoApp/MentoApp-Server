package com.mentit.mento.domain.users.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "회원 정보 조회 응답")
public class FindUserResponse {
    private Long id;
    private String name;
    private String nickname;
    private String profileImage;
    private String phoneNumber;
    private String simpleIntroduce;

    private int dotoriTokenAmount;

    private List<String> boardKeywordList;

    private String personalHistory;

    private List<String> myStatus;

    private String corporateForm;

    private String userJob;


}
