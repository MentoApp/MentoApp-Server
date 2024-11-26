package com.mentit.mento.domain.dotoriToken.repository;

import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DotoriTokenJPARepository extends JpaRepository<DotoriTokenEntity,Long> {
    DotoriTokenEntity findByUsersEntity(UsersEntity usersEntity);
}
