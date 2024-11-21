package com.mentit.mento.domain.dotoriToken.repository;

import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.dotoriToken.service.port.DotoriTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DotoriTokenRepositoryImpl implements DotoriTokenRepository {
    private final DotoriTokenJPARepository dotoriTokenJPARepository;

    @Override
    public DotoriTokenEntity save(DotoriTokenEntity dotoriTokenEntity) {
        return dotoriTokenJPARepository.save(dotoriTokenEntity);
    }
}
