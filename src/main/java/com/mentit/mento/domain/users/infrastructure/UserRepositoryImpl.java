package com.mentit.mento.domain.users.infrastructure;

import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.jpaRepository.UserJPARepository;
import com.mentit.mento.domain.users.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final UserJPARepository userJPARepository;

    @Override
    public Optional<UsersEntity> findByEmail(String email) {
        log.info("Searching for user in repository with email: {}", email);
        Optional<UsersEntity> userEntity = userJPARepository.findByEmail(email);
        userEntity.ifPresent(entity -> 
            log.info("Found user entity with ID: {}", entity.getUserId())
        );
        return userEntity;
    }

    @Override
    public Optional<UsersEntity> findByNickname(String nickname, Long userId) {
        return userJPARepository.findByNickname(nickname,userId);
    }

    @Override
    public UsersEntity findByBoard(Long userId) {
        return userJPARepository.findByBoardEntities(userId);
    }

    @Override
    public UsersEntity save(UsersEntity usersEntity) {
        return  userJPARepository.save(usersEntity);
    }

    @Override
    public Optional<UsersEntity> findById(Long id) {
        return userJPARepository.findById(id);
    }

    @Override
    public void delete(UsersEntity usersEntity) {
        userJPARepository.delete(usersEntity);
    }

    @Override
    public void flush() {
        userJPARepository.flush();
    }
}
