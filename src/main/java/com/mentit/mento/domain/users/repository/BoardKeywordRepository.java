package com.mentit.mento.domain.users.repository;

import com.mentit.mento.domain.users.constant.BoardKeyword;
import com.mentit.mento.domain.users.entity.BoardKeywordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardKeywordRepository extends JpaRepository<BoardKeywordEntity, Long> {
}
