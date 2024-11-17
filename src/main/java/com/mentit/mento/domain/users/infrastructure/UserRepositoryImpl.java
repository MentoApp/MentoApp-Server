package com.mentit.mento.domain.users.infrastructure;

import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.jpaRepository.UserJPARepository;
import com.mentit.mento.domain.users.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJPARepository userJPARepository;


    @Override
    public Optional<UsersEntity> findByEmail(String email) {
        return userJPARepository.findByEmail(email);
    }

    @Override
    public Optional<UsersEntity> findByNickname(String nickname, Long userId) {
        return userJPARepository.findByNickname(nickname,userId);
    }

    @Override
    public UsersEntity findByBoard(Long userId) {
        return userJPARepository.findByBoard(userId);
    }

    @Override
    public Users save(Users modifiedUser) {
        return userJPARepository.save(UsersEntity.from(modifiedUser)).to();
    }
}
