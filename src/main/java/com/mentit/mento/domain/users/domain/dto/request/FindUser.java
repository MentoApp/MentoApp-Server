package com.mentit.mento.domain.users.domain.dto.request;

import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.constant.UserGenderEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FindUser {
    private Long id;
    private String name;
    private String email;
    private String organization;
    private String job;
    private String preferredJob;
    private String nickname;
    private UserGenderEnum gender;
    private LocalDate birthyear;
    private LocalDate birthday;
    private AuthType authType;

}
