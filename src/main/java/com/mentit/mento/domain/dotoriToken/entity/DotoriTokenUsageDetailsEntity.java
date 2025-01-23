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
    @Column(name = "dotori_token_usage_detail_id")
    private Long dotoriTokenUsageDetailId;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type")
    private TradeTypeEnum tradeTypeEnum;

    @Column(name = "trade_amount")
    private int tradeAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presenter_id")
    private UsersEntity presenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private UsersEntity receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dotori_token_id")
    private DotoriTokenEntity dotoriTokenEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = Boolean.FALSE;
}