package com.mentit.mento.domain.users.infrastructure;

import com.mentit.mento.domain.users.domain.UserStatusTag;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.jpaRepository.UserStatusTagJPARepository;
import com.mentit.mento.domain.users.service.port.UserStatusTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserStatusTagRepositoryImpl implements UserStatusTagRepository {

    private final UserStatusTagJPARepository userStatusTagJPARepository;


    @Override
    public void deleteAllByUsers(UsersEntity usersEntity) {

    }

    @Override
    public Optional<UserStatusTagEntity> findByUsers(UsersEntity usersEntity) {
        return userStatusTagJPARepository.findByUsersEntity(usersEntity);
    }

    @Override
    public UserStatusTagEntity save(UserStatusTagEntity userStatusTagEntity) {
        log.info("Saved UserStatusTag ID: {}", userStatusTagEntity.getUserStatusTagId());
        return userStatusTagJPARepository.save(userStatusTagEntity);
    }

    @Override
    public void delete(UserStatusTagEntity userStatusTagEntity) {
        userStatusTagJPARepository.delete(userStatusTagEntity);
    }
}
