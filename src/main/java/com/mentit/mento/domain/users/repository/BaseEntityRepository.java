package com.mentit.mento.domain.users.repository;

import com.mentit.mento.domain.users.entity.BaseTagEntity;
import com.mentit.mento.global.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseEntityRepository extends JpaRepository<BaseTagEntity, Long> {
}
