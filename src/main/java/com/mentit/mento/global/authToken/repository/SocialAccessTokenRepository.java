package com.mentit.mento.global.authToken.repository;

import com.mentit.mento.domain.users.entity.Users;
import com.mentit.mento.global.authToken.entity.SocialAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccessTokenRepository extends JpaRepository<SocialAccessToken, Long> {
    Optional<SocialAccessToken> findByUser(Users existingUser);
}
