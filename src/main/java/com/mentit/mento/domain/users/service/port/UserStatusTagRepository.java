package com.mentit.mento.domain.users.service.port;

import com.mentit.mento.domain.users.domain.UserStatusTag;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;

import java.util.Optional;

public interface UserStatusTagRepository {
    void deleteAllByUsers(Users findUserByUserDetail);

    Optional<UserStatusTag> findByUsers(Users user);

    UserStatusTag save(UserStatusTag userStatusTag);

    void delete(UserStatusTag findUserStatusTag);
}
