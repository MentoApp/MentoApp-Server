package com.mentit.mento.domain.users.repository;

import com.mentit.mento.domain.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    @Query("select u from Users u where u.email = :email and u.isDeleted=false")
    Optional<Users> findByEmail(String email);


    @Query("select u from Users u where u.userId !=:userId And u.nickname=:nickname And u.isDeleted=false ")
    Optional<Users> findByNickname(String nickname, Long userId);
}
