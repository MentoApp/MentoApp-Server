package com.mentit.mento.domain.dotoriToken.service.port;

import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetails;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetailsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DotoriTokenUsageDetailsRepository {
    DotoriTokenUsageDetails save(DotoriTokenUsageDetails dotoriTokenUsageDetails);

    Page<DotoriTokenUsageDetailsEntity> findByDotoriToken(DotoriToken dotoriToken, Pageable pageable);
}
