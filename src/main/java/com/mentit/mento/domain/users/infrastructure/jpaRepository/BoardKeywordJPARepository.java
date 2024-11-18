package com.mentit.mento.domain.users.infrastructure.jpaRepository;

import com.mentit.mento.domain.users.constant.BoardKeywordEnum;
import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardKeywordJPARepository extends JpaRepository<BoardKeywordEntity, Long> {
    void deleteAllByUsers(UsersEntity findUserByUserDetail);
}
