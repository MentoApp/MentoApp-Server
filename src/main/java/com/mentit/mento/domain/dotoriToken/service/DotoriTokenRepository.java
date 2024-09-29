package com.mentit.mento.domain.dotoriToken.service;

import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DotoriTokenRepository extends JpaRepository<DotoriToken,Long> {
}
