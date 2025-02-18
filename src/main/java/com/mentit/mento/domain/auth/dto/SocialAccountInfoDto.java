package com.mentit.mento.domain.auth.dto;

import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.constant.UserGenderEnum;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.global.security.util.PasswordUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialAccountInfoDto {
    private String authType;
    private String name;
    private String email;
    private String gender;
    private String birthDay;
    private String birthYear;
    private String phoneNumber;
    private String socialAccessToken;

    public static UsersEntity to(SocialAccountInfoDto socialAccountInfoDto) {
        return UsersEntity.builder()
                .email(socialAccountInfoDto.getEmail())
                .name(socialAccountInfoDto.getName())
                .authType(AuthType.of(socialAccountInfoDto.getAuthType()))
                .gender(UserGenderEnum.valueOf(socialAccountInfoDto.getGender()))
                .birthYear(socialAccountInfoDto.getBirthYear())
                .birthDay(socialAccountInfoDto.getBirthDay())
                .phoneNumber(socialAccountInfoDto.getPhoneNumber())
                .isNewUser(true)
                .password(PasswordUtil.generateRandomPassword())
                .build();
    }
}
