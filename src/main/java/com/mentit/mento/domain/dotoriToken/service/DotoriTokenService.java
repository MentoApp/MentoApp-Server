package com.mentit.mento.domain.dotoriToken.service;

import com.mentit.mento.domain.dotoriToken.constant.TradeType;
import com.mentit.mento.domain.dotoriToken.dto.request.DotoriUsageResponse;
import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetails;
import com.mentit.mento.domain.users.entity.Users;
import com.mentit.mento.domain.users.repository.UserRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DotoriTokenService {

    private final DotoriTokenRepository dotoriTokenRepository;
    private final DotoriTokenUsageDetailsRepository dotoriTokenUsageDetailsRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createDotoriToken(Users findUserByUserDetail) {
        DotoriToken dotoriToken = DotoriToken.builder()
                .users(findUserByUserDetail)
                .count(5)
                .build();
        dotoriTokenRepository.save(dotoriToken);

        //TODO:: 운영진이 주는 경우 어떻게 처리할것인가? -> 운영진 계정 필요?
        DotoriTokenUsageDetails dotoriTokenUsageDetails = DotoriTokenUsageDetails.builder()
                .receiver(findUserByUserDetail)
                .tradeAmount(5)
                .tradeType(TradeType.EARN)
                .dotoriToken(dotoriToken)
                .build();
        dotoriTokenUsageDetailsRepository.save(dotoriTokenUsageDetails);
    }

    private Users getUsers(CustomUserDetail userDetail) {
        return userRepository.findById(userDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }
}