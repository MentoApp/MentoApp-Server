package com.mentit.mento.domain.dotoriToken.entity;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.dotoriToken.constant.TradeTypeEnum;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "dotoriTokenUsageDetails")
@SQLDelete(sql = " update dotori_token_usage_details set is_deleted = true where user_id = ?")
public class DotoriTokenUsageDetailsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dotoriTokenUsageDetailId;

    @Enumerated(EnumType.STRING)
    private TradeTypeEnum tradeTypeEnum;

    private int tradeAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presenter_id")  // 기부하는 유저
    private UsersEntity presenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")   // 도토리를 받는 유저
    private UsersEntity receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dotori_token_id")
    private DotoriTokenEntity dotoriTokenEntity;

    @OneToOne(mappedBy = "dotoriTokenUsageDetail")
    private BoardEntity boardEntity;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = Boolean.FALSE;

    public DotoriTokenUsageDetails to() {
        return  DotoriTokenUsageDetails.builder()
                .dotoriTokenUsageDetailId(dotoriTokenUsageDetailId)
                .tradeTypeEnum(tradeTypeEnum)
                .tradeAmount(tradeAmount)
                .presenter(presenter.to())
                .receiver(receiver.to())
                .dotoriTokenEntity(dotoriTokenEntity.to())
                .boardEntity(boardEntity.to())
                .isDeleted(isDeleted)
                .build();
    }

    public DotoriTokenUsageDetails toCreateUsage() {
        return  DotoriTokenUsageDetails.builder()
                .dotoriTokenUsageDetailId(dotoriTokenUsageDetailId)
                .tradeTypeEnum(tradeTypeEnum)
                .tradeAmount(tradeAmount)
                .presenter(null)
                .receiver(receiver.to())
                .dotoriTokenEntity(dotoriTokenEntity.to())
                .boardEntity(null)
                .isDeleted(isDeleted)
                .build();
    }
}
