package com.mentit.mento.domain.users.dto.request;

import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.constant.UserGender;
import com.mentit.mento.domain.users.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FindUserDTO {
    private Long id;
    private String name;
    private String email;
    private String organization;
    private String job;
    private String preferredJob;
    private String nickname;
    private UserGender gender;
    private LocalDate birthyear;
    private LocalDate birthday;
    private AuthType authType;
    private Role roleType;


}
