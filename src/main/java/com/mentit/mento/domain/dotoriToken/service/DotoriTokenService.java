package com.mentit.mento.domain.dotoriToken.service;

import com.mentit.mento.domain.dotoriToken.constant.TradeType;
import com.mentit.mento.domain.dotoriToken.dto.response.DotoriEarnRseponse;
import com.mentit.mento.domain.dotoriToken.dto.response.DotoriGiveResponse;
import com.mentit.mento.domain.dotoriToken.dto.response.DotoriUsageResponse;
import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetails;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.UserRepositoryImpl;
import com.mentit.mento.global.exception.ExceptionCode;
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
public class DotoriTokenService {

    private final DotoriTokenRepository dotoriTokenRepository;
    private final DotoriTokenUsageDetailsRepository dotoriTokenUsageDetailsRepository;
    private final UserRepositoryImpl userRepositoryImpl;

    @Transactional
    public void createDotoriToken(UsersEntity findUserByUserDetail) {
        DotoriToken dotoriToken = DotoriToken.builder()
                .usersEntity(findUserByUserDetail)
                .count(5)
                .build();
        dotoriTokenRepository.save(dotoriToken);

        //TODO:: 운영진이 주는 경우 어떻게 처리할것인가? -> 운영진 계정 필요?
        DotoriTokenUsageDetails dotoriTokenUsageDetails = DotoriTokenUsageDetails.builder()
                .receiver(findUserByUserDetail)
                .tradeAmount(5)
                .tradeType(TradeType.BOARD_EARN)
                .dotoriToken(dotoriToken)
                .build();
        dotoriTokenUsageDetailsRepository.save(dotoriTokenUsageDetails);
    }

    private Users getUsers(CustomUserDetail userDetail) {
        return userRepositoryImpl.findById(userDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }

    public Page<DotoriUsageResponse> findUsage(Pageable pageable, CustomUserDetail customUserDetail) {
        Users findUserByUserDetail = getUsers(customUserDetail);

        DotoriToken dotoriToken = findUserByUserDetail.getDotoriToken();

        Page<DotoriTokenUsageDetails> usageList = dotoriTokenUsageDetailsRepository.findByDotoriToken(dotoriToken,pageable);


        List<DotoriUsageResponse> DotoriUsageResponseList = usageList.stream().map(
                dotoriTokenUsageDetails -> {
                    List<DotoriGiveResponse> giveResponseList = new ArrayList<>();
                    List<DotoriEarnRseponse> earnRseponseList = new ArrayList<>();
                    //게시물에 도토리 선물
                    if(dotoriTokenUsageDetails.getTradeType() == TradeType.BOARD_EARN) {
                        DotoriEarnRseponse dotoriEarnRseponse = DotoriEarnRseponse.builder()
                                .usageCount("+"+dotoriTokenUsageDetails.getTradeAmount()+"개")
                                .boardId(dotoriTokenUsageDetails.getBoardEntity().getBoardId())
                                .boardTitle(dotoriTokenUsageDetails.getBoardEntity().getTitle())
                                .senderId(dotoriTokenUsageDetails.getPresenter().getUserId())
                                .timestamp(dotoriTokenUsageDetails.getCreatedAt())
                                .tradeType(dotoriTokenUsageDetails.getTradeType().getTradeType())
                                .message(dotoriTokenUsageDetails.getPresenter().getNickname()+"에게 선물을 받았습니다.")
                                .build();
                        earnRseponseList.add(dotoriEarnRseponse);
                    //게시물 생성시
                    } else if (dotoriTokenUsageDetails.getTradeType() == TradeType.BOARD_CREATE) {
                        DotoriEarnRseponse dotoriEarnRseponse = DotoriEarnRseponse.builder()
                                .usageCount("+"+dotoriTokenUsageDetails.getTradeAmount()+"개")
                                .boardId(dotoriTokenUsageDetails.getBoardEntity().getBoardId())
                                .boardTitle(dotoriTokenUsageDetails.getBoardEntity().getTitle())
                                .senderId(dotoriTokenUsageDetails.getPresenter().getUserId())
                                .timestamp(dotoriTokenUsageDetails.getCreatedAt())
                                .tradeType(dotoriTokenUsageDetails.getTradeType().getTradeType())
                                .message("게시물 작성 적립")
                                .build();
                        earnRseponseList.add(dotoriEarnRseponse);
                    //충전
                    } else if (dotoriTokenUsageDetails.getTradeType() == TradeType.CHARGING) {
                        DotoriEarnRseponse dotoriEarnRseponse = DotoriEarnRseponse.builder()
                                .usageCount("+"+dotoriTokenUsageDetails.getTradeAmount()+"개")
                                .senderId(dotoriTokenUsageDetails.getPresenter().getUserId())
                                .timestamp(dotoriTokenUsageDetails.getCreatedAt())
                                .tradeType(dotoriTokenUsageDetails.getTradeType().getTradeType())
                                .message("원 결제")//TODO 교환 환율에 맞춰 적기 만약 1000:20이고 40개 충전했다면 40*100적기
                                .build();
                        earnRseponseList.add(dotoriEarnRseponse);
                    // 가입
                    } else if (dotoriTokenUsageDetails.getTradeType() == TradeType.ENROLLMENT) {
                        DotoriEarnRseponse dotoriEarnRseponse = DotoriEarnRseponse.builder()
                                .usageCount("+"+dotoriTokenUsageDetails.getTradeAmount()+"개")
                                .senderId(dotoriTokenUsageDetails.getPresenter().getUserId())
                                .timestamp(dotoriTokenUsageDetails.getCreatedAt())
                                .tradeType(dotoriTokenUsageDetails.getTradeType().getTradeType())
                                .message("도토리 가입 축하 적립 ")
                                .build();
                        earnRseponseList.add(dotoriEarnRseponse);
                    } else{
                        //게시물에 선물
                        DotoriGiveResponse dotoriGiveResponse = DotoriGiveResponse.builder()
                                .usageCount("-"+dotoriTokenUsageDetails.getTradeAmount()+"개")
                                .boardId(dotoriTokenUsageDetails.getBoardEntity().getBoardId())
                                .boardTitle(dotoriTokenUsageDetails.getBoardEntity().getTitle())
                                .receiverId(dotoriTokenUsageDetails.getReceiver().getUserId())
                                .timestamp(dotoriTokenUsageDetails.getCreatedAt())
                                .tradeType(dotoriTokenUsageDetails.getTradeType().getTradeType())
                                .message(dotoriTokenUsageDetails.getReceiver().getNickname()+"에게 선물을 보냈습니다.")
                                .build();
                        giveResponseList.add(dotoriGiveResponse);
                    }
                    return DotoriUsageResponse.builder()
                            .dotoriGiveResponseList(giveResponseList)
                            .dotoriEarnRseponseList(earnRseponseList)
                            .build();
                })
                .toList();
            return new PageImpl<>(DotoriUsageResponseList,pageable,usageList.getTotalElements());
    }
}