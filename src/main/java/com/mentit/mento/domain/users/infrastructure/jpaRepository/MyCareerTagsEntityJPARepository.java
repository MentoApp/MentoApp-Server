package com.mentit.mento.domain.users.infrastructure.jpaRepository;

import com.mentit.mento.domain.users.domain.entity.MyCareerTagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyCareerTagsEntityJPARepository extends JpaRepository<MyCareerTagsEntity, Long> {
}
