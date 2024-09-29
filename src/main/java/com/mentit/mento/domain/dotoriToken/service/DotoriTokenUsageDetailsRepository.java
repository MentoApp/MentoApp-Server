package com.mentit.mento.domain.dotoriToken.service;

import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DotoriTokenUsageDetailsRepository extends JpaRepository<DotoriTokenUsageDetails,Long> {
}
