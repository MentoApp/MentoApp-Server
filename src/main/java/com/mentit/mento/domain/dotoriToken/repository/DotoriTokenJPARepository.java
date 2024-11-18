package com.mentit.mento.domain.dotoriToken.repository;

import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DotoriTokenJPARepository extends JpaRepository<DotoriTokenEntity,Long> {
}
