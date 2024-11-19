package com.mentit.mento.domain.dotoriToken.service.port;

import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;

public interface DotoriTokenRepository {
    DotoriToken save(DotoriToken dotoriToken);
}
