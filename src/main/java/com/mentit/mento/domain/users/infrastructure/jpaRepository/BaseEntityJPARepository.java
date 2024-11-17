package com.mentit.mento.domain.users.infrastructure.jpaRepository;

import com.mentit.mento.global.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseEntityJPARepository extends JpaRepository<BaseEntity, Long> {
}
