package com.mentit.mento.domain.auth.mapper;

import com.mentit.mento.domain.auth.constant.AuthType;
import com.mentit.mento.domain.auth.constant.UserGender;
import com.mentit.mento.domain.auth.entity.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    Users toEntity(String email, String name, String profileImage, AuthType authType, String nickname, UserGender userGender, int birthday, int birthyear);
}
