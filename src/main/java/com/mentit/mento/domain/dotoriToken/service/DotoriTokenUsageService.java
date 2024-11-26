package com.mentit.mento.domain.dotoriToken.service;

import com.mentit.mento.domain.dotoriToken.constant.TradeTypeEnum;
import com.mentit.mento.domain.dotoriToken.dto.TokenGiftRequest;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetailsEntity;
import com.mentit.mento.domain.dotoriToken.service.port.DotoriTokenUsageDetailsRepository;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.service.port.UserRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DotoriTokenUsageService {

    private final DotoriTokenUsageDetailsRepository dotoriTokenUsageDetailsRepository;
    private final UserRepository userRepository;

    public void create(CustomUserDetail customUserDetail , TokenGiftRequest tokenGiftRequest, DotoriTokenEntity dotoriTokenEntity) {
        UsersEntity usersEntity = getUsers(customUserDetail.getId());
        DotoriTokenUsageDetailsEntity.builder()
                .tradeTypeEnum(TradeTypeEnum.BOARD_GIVE_BY_USER)
                .dotoriTokenEntity(dotoriTokenEntity)
                .tradeAmount(tokenGiftRequest.getTradeAmount())
                .presenter(getUsers(tokenGiftRequest.getPresent_id()))
                .receiver(getUsers(tokenGiftRequest.getReceiver_id()))
                .build();
    }

    private UsersEntity getUsers(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }
}
