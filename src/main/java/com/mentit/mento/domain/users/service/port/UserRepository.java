package com.mentit.mento.domain.users.service.port;

import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;

import java.util.Optional;

public interface UserRepository {


    Optional<Users> findByEmail(String email);


    Optional<Users> findByNickname(String nickname, Long userId);

    Users findByBoard(Long userId);

    UsersEntity save(Users modifiedUser);

    Optional<UsersEntity> findById(Long id);

    void delete(UsersEntity findUser);

    void flush();
}
