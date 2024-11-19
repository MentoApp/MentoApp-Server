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
    public Optional<Users> findByEmail(String email) {
        return userJPARepository.findByEmail(email).map(UsersEntity::to);
    }

    @Override
    public Optional<Users> findByNickname(String nickname, Long userId) {
        return userJPARepository.findByNickname(nickname,userId).map(UsersEntity::to);
    }

    @Override
    public Users findByBoard(Long userId) {
        return userJPARepository.findByBoardEntities(userId).to();
    }

    @Override
    public Users save(Users modifiedUser) {
        return userJPARepository.save(UsersEntity.from(modifiedUser)).to();
    }

    @Override
    public Optional<Users> findById(Long id) {
        return userJPARepository.findById(id).map(UsersEntity::to);
    }

    @Override
    public void delete(Users findUser) {
        userJPARepository.delete(UsersEntity.from(findUser));
    }

    @Override
    public void flush() {
        userJPARepository.flush();
    }
}
