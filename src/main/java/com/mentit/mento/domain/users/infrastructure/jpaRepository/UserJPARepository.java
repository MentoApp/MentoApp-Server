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

    @Query("select u from UsersEntity u where u.userId !=:userId And u.isDeleted=false And u.nickname=:nickname  ")
    Optional<UsersEntity> findByNickname(String nickname, Long userId);

    @Query("select u from UsersEntity u where u.userId= :userId")
    UsersEntity findByBoardEntities(Long userId);
}
