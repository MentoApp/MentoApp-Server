package com.mentit.mento.domain.dotoriToken.service.port;

import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;

public interface DotoriTokenRepository {
    DotoriTokenEntity save(DotoriTokenEntity dotoriToken);
}
