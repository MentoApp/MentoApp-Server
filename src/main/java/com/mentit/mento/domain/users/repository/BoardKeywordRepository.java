package com.mentit.mento.domain.users.repository;

import com.mentit.mento.domain.users.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardKeywordRepository extends JpaRepository<BoardKeywordEntity, Long> {
    void deleteAllByUsers(Users findUserByUserDetail);
}
