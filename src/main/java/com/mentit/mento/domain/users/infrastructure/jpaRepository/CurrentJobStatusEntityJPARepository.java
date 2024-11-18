package com.mentit.mento.domain.users.infrastructure.jpaRepository;

import com.mentit.mento.domain.users.domain.entity.CurrentJobStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentJobStatusEntityJPARepository extends JpaRepository<CurrentJobStatusEntity, Long> {
}
