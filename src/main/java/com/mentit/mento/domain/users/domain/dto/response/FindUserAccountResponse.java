package com.mentit.mento.domain.users.domain.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
