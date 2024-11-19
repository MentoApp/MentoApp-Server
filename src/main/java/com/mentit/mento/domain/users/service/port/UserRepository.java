package com.mentit.mento.domain.users.service.port;

import com.mentit.mento.domain.users.domain.Users;

import java.util.Optional;

public interface UserRepository {


    Optional<Users> findByEmail(String email);


    Optional<Users> findByNickname(String nickname, Long userId);

    Users findByBoard(Long userId);

    Users save(Users modifiedUser);

    Optional<Users> findById(Long id);

    void delete(Users findUser);

    void flush();
}
