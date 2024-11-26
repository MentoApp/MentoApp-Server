package com.mentit.mento.domain.dotoriToken.service;

import com.mentit.mento.domain.dotoriToken.constant.TradeTypeEnum;
import com.mentit.mento.domain.dotoriToken.dto.TokenGiftRequest;
import com.mentit.mento.domain.dotoriToken.dto.response.DotoriEarnRseponse;
import com.mentit.mento.domain.dotoriToken.dto.response.DotoriGiveResponse;
import com.mentit.mento.domain.dotoriToken.dto.response.DotoriUsageResponse;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetailsEntity;
import com.mentit.mento.domain.dotoriToken.service.port.DotoriTokenRepository;
import com.mentit.mento.domain.dotoriToken.service.port.DotoriTokenUsageDetailsRepository;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.service.port.UserRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.DotoriTokenException;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DotoriTokenService {

    private final DotoriTokenRepository dotoriTokenRepository;
    private final DotoriTokenUsageDetailsRepository dotoriTokenUsageDetailsRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createDotoriToken(UsersEntity usersEntity) {
        DotoriTokenEntity dotoriToken = DotoriTokenEntity.builder()
                .usersEntity(usersEntity)
                .count(5)
                .build();
        dotoriTokenRepository.save(dotoriToken);

        //TODO:: 운영진이 주는 경우 어떻게 처리할것인가? -> 운영진 계정 필요?
        DotoriTokenUsageDetailsEntity dotoriTokenUsageDetails = DotoriTokenUsageDetailsEntity.builder()
                .receiver(usersEntity)
                .tradeAmount(5)
                .tradeTypeEnum(TradeTypeEnum.ENROLLMENT)
                .dotoriTokenEntity(dotoriToken)
                .build();
        dotoriTokenUsageDetailsRepository.saveCreateAccount(dotoriTokenUsageDetails);
    }

    private UsersEntity getUsers(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }

    public Page<DotoriUsageResponse> findUsage(Pageable pageable, CustomUserDetail customUserDetail) {
        UsersEntity findUserByUserDetail = getUsers(customUserDetail.getId());

        DotoriTokenEntity dotoriToken = findUserByUserDetail.getDotoriTokenEntity();

        Page<DotoriTokenUsageDetailsEntity> usageList = dotoriTokenUsageDetailsRepository.findByDotoriToken(dotoriToken, pageable);

        List<DotoriGiveResponse> giveResponseList = new ArrayList<>();
        List<DotoriEarnRseponse> earnRseponseList = new ArrayList<>();

       usageList.forEach(
                dotoriTokenUsageDetails -> {
                    //게시물에 도토리 선물
                    if (dotoriTokenUsageDetails.getTradeTypeEnum() == TradeTypeEnum.BOARD_EARN) {
                        DotoriEarnRseponse dotoriEarnRseponse = DotoriEarnRseponse.builder()
                                .usageCount("+" + dotoriTokenUsageDetails.getTradeAmount() + "개")
                                .boardId(dotoriTokenUsageDetails.getBoardEntity().getBoardId())
                                .boardTitle(dotoriTokenUsageDetails.getBoardEntity().getTitle())
                                .senderId(dotoriTokenUsageDetails.getPresenter().getUserId())
                                .timestamp(dotoriTokenUsageDetails.getCreatedAt())
                                .tradeType(dotoriTokenUsageDetails.getTradeTypeEnum().getTradeType())
                                .message(dotoriTokenUsageDetails.getPresenter().getNickname() + "에게 선물을 받았습니다.")
                                .build();
                        earnRseponseList.add(dotoriEarnRseponse);
                        //게시물 생성시
                    } else if (dotoriTokenUsageDetails.getTradeTypeEnum() == TradeTypeEnum.BOARD_CREATE) {
                        DotoriEarnRseponse dotoriEarnRseponse = DotoriEarnRseponse.builder()
                                .usageCount("+" + dotoriTokenUsageDetails.getTradeAmount() + "개")
                                .boardId(dotoriTokenUsageDetails.getBoardEntity().getBoardId())
                                .boardTitle(dotoriTokenUsageDetails.getBoardEntity().getTitle())
                                .senderId(dotoriTokenUsageDetails.getPresenter().getUserId())
                                .timestamp(dotoriTokenUsageDetails.getCreatedAt())
                                .tradeType(dotoriTokenUsageDetails.getTradeTypeEnum().getTradeType())
                                .message("게시물 작성 적립")
                                .build();
                        earnRseponseList.add(dotoriEarnRseponse);
                        //충전
                    } else if (dotoriTokenUsageDetails.getTradeTypeEnum() == TradeTypeEnum.CHARGING) {
                        DotoriEarnRseponse dotoriEarnRseponse = DotoriEarnRseponse.builder()
                                .usageCount("+" + dotoriTokenUsageDetails.getTradeAmount() + "개")
                                .senderId(dotoriTokenUsageDetails.getPresenter().getUserId())
                                .timestamp(dotoriTokenUsageDetails.getCreatedAt())
                                .tradeType(dotoriTokenUsageDetails.getTradeTypeEnum().getTradeType())
                                .message("원 결제")
                                .build();
                        earnRseponseList.add(dotoriEarnRseponse);
                        // 가입
                    } else if (dotoriTokenUsageDetails.getTradeTypeEnum() == TradeTypeEnum.ENROLLMENT) {
                        DotoriEarnRseponse dotoriEarnRseponse = DotoriEarnRseponse.builder()
                                .usageCount("+" + dotoriTokenUsageDetails.getTradeAmount() + "개")
                                .senderId(dotoriTokenUsageDetails.getPresenter() == null ? null : dotoriTokenUsageDetails.getPresenter().getUserId())
                                .timestamp(dotoriTokenUsageDetails.getCreatedAt())
                                .tradeType(dotoriTokenUsageDetails.getTradeTypeEnum().getTradeType())
                                .message("도토리 가입 축하 적립")
                                .build();
                        earnRseponseList.add(dotoriEarnRseponse);
                    } else {
                        //게시물에 선물
                        DotoriGiveResponse dotoriGiveResponse = DotoriGiveResponse.builder()
                                .usageCount("-" + dotoriTokenUsageDetails.getTradeAmount() + "개")
                                .boardId(dotoriTokenUsageDetails.getBoardEntity().getBoardId())
                                .boardTitle(dotoriTokenUsageDetails.getBoardEntity().getTitle())
                                .receiverId(dotoriTokenUsageDetails.getReceiver().getUserId())
                                .timestamp(dotoriTokenUsageDetails.getCreatedAt())
                                .tradeType(dotoriTokenUsageDetails.getTradeTypeEnum().getTradeType())
                                .message(dotoriTokenUsageDetails.getReceiver().getNickname() + "에게 선물을 보냈습니다.")
                                .build();
                        giveResponseList.add(dotoriGiveResponse);
                    }
                });
        // 하나의 DotoriUsageResponse에 두 리스트를 포함하여 반환
        DotoriUsageResponse dotoriUsageResponse = DotoriUsageResponse.builder()
                .dotoriGiveResponseList(giveResponseList.isEmpty() ? null : giveResponseList)
                .dotoriEarnRseponseList(earnRseponseList.isEmpty() ? null : earnRseponseList)
                .build();
        return new PageImpl<>(List.of(dotoriUsageResponse), pageable, usageList.getTotalElements());
    }

    public DotoriTokenEntity updateDotoriToken(CustomUserDetail customUserDetail, TokenGiftRequest tokenGiftRequest) {
        UsersEntity presentUsersEntity = getUsers(customUserDetail.getId());

        DotoriTokenEntity dotoriTokenEntity = dotoriTokenRepository.findByUsersEntity(presentUsersEntity);
        dotoriTokenEntity.toBuilder()
                .count(dotoriTokenEntity.getCount() - tokenGiftRequest.getTradeAmount())
                .build();


        DotoriTokenEntity receiverTokenEntity = dotoriTokenRepository.findByUsersEntity(getUsers(tokenGiftRequest.getReceiver_id()));
        receiverTokenEntity.toBuilder().count(receiverTokenEntity.getCount() + tokenGiftRequest.getTradeAmount())
                .build();

        dotoriTokenRepository.save(receiverTokenEntity);

        return dotoriTokenRepository.save(dotoriTokenEntity);
    }

    public int getTokenCount(CustomUserDetail customUserDetail) {
        UsersEntity usersEntity = getUsers(customUserDetail.getId());
        if (!usersEntity.getDotoriTokenEntity().isDeleted()) {
            return usersEntity.getDotoriTokenEntity().getCount();
        } else {
            throw new DotoriTokenException(ExceptionCode.NOT_FOUND_DOTORI_TOKEN);
        }
    }
}