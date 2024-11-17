package com.mentit.mento.domain.users.infrastructure.jpaRepository;

import com.mentit.mento.domain.users.domain.entity.MyStatusTagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyStatusTagsEntityJPARepository extends JpaRepository<MyStatusTagsEntity, Long> {
}
