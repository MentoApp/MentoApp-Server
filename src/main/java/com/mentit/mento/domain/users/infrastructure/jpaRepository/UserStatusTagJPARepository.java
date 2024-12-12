package com.mentit.mento.domain.users.infrastructure.jpaRepository;

import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatusTagJPARepository extends JpaRepository<UserStatusTagEntity, Long> {
    void deleteAllByUsersEntity(UsersEntity findUserByUserDetail);

    Optional<UserStatusTagEntity> findByUsersEntity(UsersEntity user);
}
