package com.mentit.mento.domain.dotoriToken.repository;

import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
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
    public DotoriTokenUsageDetailsEntity save(DotoriTokenUsageDetailsEntity dotoriTokenUsageDetails) {
        return dotoriTokenUsageDetailsJPARepository.save(dotoriTokenUsageDetails);
    }

    @Override
    public Page<DotoriTokenUsageDetailsEntity> findByDotoriToken(DotoriTokenEntity dotoriToken, Pageable pageable) {
        return dotoriTokenUsageDetailsJPARepository.findByDotoriTokenEntity(dotoriToken, pageable);
    }

    @Override
    public DotoriTokenUsageDetailsEntity saveCreateAccount(DotoriTokenUsageDetailsEntity dotoriTokenUsageDetails) {
        return dotoriTokenUsageDetailsJPARepository.save(dotoriTokenUsageDetails);

    }
}
