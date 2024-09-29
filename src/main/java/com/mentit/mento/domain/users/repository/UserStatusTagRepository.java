package com.mentit.mento.domain.users.repository;

import com.mentit.mento.domain.users.entity.UserStatusTag;
import com.mentit.mento.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatusTagRepository extends JpaRepository<UserStatusTag, Long> {
    void deleteAllByUsers(Users findUserByUserDetail);

    Optional<UserStatusTag> findByUsers(Users user);
}
