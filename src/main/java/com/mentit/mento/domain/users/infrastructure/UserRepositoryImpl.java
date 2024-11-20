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
    public Optional<Users> findByEmail(String email) {
        log.info("Searching for user in repository with email: {}", email);
        Optional<UsersEntity> userEntity = userJPARepository.findByEmail(email);
        userEntity.ifPresent(entity -> 
            log.info("Found user entity with ID: {}", entity.getUserId())
        );
        return userEntity.map(UsersEntity::to);
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
    public UsersEntity save(Users user) {
        log.info("Saving user with email: {}", user.getEmail());
        UsersEntity savedEntity = userJPARepository.save(UsersEntity.from(user));
        log.info("Saved user with ID: {}", savedEntity.getUserId());
        return savedEntity;
    }

    @Override
    public Optional<UsersEntity> findById(Long id) {
        return userJPARepository.findById(id);
    }

    @Override
    public void delete(UsersEntity findUser) {
        userJPARepository.delete(findUser);
    }

    @Override
    public void flush() {
        userJPARepository.flush();
    }
}
