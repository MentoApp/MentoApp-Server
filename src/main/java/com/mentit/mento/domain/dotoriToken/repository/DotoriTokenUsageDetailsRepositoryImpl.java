package com.mentit.mento.domain.dotoriToken.repository;

import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetails;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetailsEntity;
import com.mentit.mento.domain.dotoriToken.service.port.DotoriTokenUsageDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DotoriTokenUsageDetailsRepositoryImpl implements DotoriTokenUsageDetailsRepository {
    private final DotoriTokenUsageDetailsJPARepository dotoriTokenUsageDetailsJPARepository;

    @Override
    public DotoriTokenUsageDetails save(DotoriTokenUsageDetails dotoriTokenUsageDetails) {
        return dotoriTokenUsageDetailsJPARepository.save(DotoriTokenUsageDetailsEntity.from(dotoriTokenUsageDetails)).to();
    }

    @Override
    public Page<DotoriTokenUsageDetailsEntity> findByDotoriToken(DotoriToken dotoriToken, Pageable pageable) {
        return dotoriTokenUsageDetailsJPARepository.findByDotoriTokenEntity(DotoriTokenEntity.from(dotoriToken), pageable);
    }
}
