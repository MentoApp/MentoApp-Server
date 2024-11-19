package com.mentit.mento.domain.dotoriToken.repository;

import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetailsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DotoriTokenUsageDetailsJPARepository extends JpaRepository<DotoriTokenUsageDetailsEntity,Long> {
    Page<DotoriTokenUsageDetailsEntity> findByDotoriTokenEntity(DotoriTokenEntity dotoriTokenEntity, Pageable pageable);
}
