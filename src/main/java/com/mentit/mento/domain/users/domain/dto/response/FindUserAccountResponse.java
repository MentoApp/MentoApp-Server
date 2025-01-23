package com.mentit.mento.domain.users.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "회원 계정 정보 조회 응답 ")
public class FindUserAccountResponse {
    //이름
    private String name;
    //이메일
    private String email;
    //휴대전화
    private String phoneNumber;
    //소셜 연동
    private String platform;
}
