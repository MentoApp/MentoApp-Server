package com.mentit.mento.global.authToken.repository;

import com.mentit.mento.global.authToken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByMemberId(Long memberId);

    boolean existsByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    Optional<RefreshToken> getRefreshTokenByMemberId(Long uuid);
}
