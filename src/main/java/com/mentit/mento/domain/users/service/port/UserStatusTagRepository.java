package com.mentit.mento.domain.users.service.port;

import com.mentit.mento.domain.users.domain.UserStatusTag;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;

import java.util.Optional;

public interface UserStatusTagRepository {
    void deleteAllByUsers(UsersEntity usersEntity);

    Optional<UserStatusTagEntity> findByUsers(UsersEntity usersEntity);

    UserStatusTagEntity save(UserStatusTagEntity userStatusTagEntity);

    void delete(UserStatusTagEntity userStatusTagEntity);
}
