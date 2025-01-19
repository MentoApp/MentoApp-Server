package com.mentit.mento.domain.auth.dto;

import lombok.Data;

@Data
public class SocialAccountInfoDto {
    private String authType;
    private String name;
    private String email;
    private String gender;
    private String birthDay;
    private String birthYear;
    private String phoneNumber;
    private String socialAccessToken;
}
