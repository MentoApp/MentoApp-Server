package com.mentit.mento.domain.dotoriToken.service.port;

import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetailsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DotoriTokenUsageDetailsRepository {
    DotoriTokenUsageDetailsEntity save(DotoriTokenUsageDetailsEntity dotoriTokenUsageDetails);

    Page<DotoriTokenUsageDetailsEntity> findByDotoriToken(DotoriTokenEntity dotoriToken, Pageable pageable);

    DotoriTokenUsageDetailsEntity saveCreateAccount(DotoriTokenUsageDetailsEntity dotoriTokenUsageDetails);
}
