package com.mentit.mento.domain.users.infrastructure.jpaRepository;

import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJPARepository extends JpaRepository<UsersEntity, Long> {
    @Query("select u from UsersEntity u where u.email = :email and u.isDeleted=false")
    Optional<UsersEntity> findByEmail(String email);

    @Query("select u from UsersEntity u where u.nickname=:nickname and u.isDeleted=false")
    Optional<UsersEntity> findByNickname(String nickname);

    @Query("select u from UsersEntity u where u.userId= :userId")
    UsersEntity findByBoardEntities(Long userId);

    Optional<UsersEntity> findByNameAndPhoneNumber(String name, String phoneNumber);
}
