package com.mentit.mento.domain.users.mapper;

import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.constant.UserGender;
import com.mentit.mento.domain.users.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "organization",ignore = true)
    @Mapping(target = "job",ignore = true)
    @Mapping(target = "preferredJob",ignore = true)
    Users toEntity(String email, String name, String profileImage, AuthType authType, String nickname, UserGender userGender, int birthday, int birthyear);
}
