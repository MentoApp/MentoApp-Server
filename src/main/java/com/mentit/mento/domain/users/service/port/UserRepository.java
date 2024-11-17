package com.mentit.mento.domain.users.service.port;

import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;

import java.util.Optional;

public interface UserRepository {


    Optional<UsersEntity> findByEmail(String email);


    Optional<UsersEntity> findByNickname(String nickname, Long userId);

    UsersEntity findByBoard(Long userId);

    Users save(Users modifiedUser);
}
