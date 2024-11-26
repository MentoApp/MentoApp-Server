package com.mentit.mento.domain.dotoriToken.service.port;

import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;

public interface DotoriTokenRepository {
    DotoriTokenEntity save(DotoriTokenEntity dotoriToken);

    DotoriTokenEntity findByUsersEntity(UsersEntity usersEntity);
}
