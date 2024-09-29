package com.mentit.mento.domain.users.repository;

import com.mentit.mento.domain.users.entity.CurrentJobStatusEntity;
import com.mentit.mento.global.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentJobStatusEntityRepository extends JpaRepository<CurrentJobStatusEntity, Long> {
}
