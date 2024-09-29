package com.mentit.mento.domain.dotoriToken.entity;

import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.dotoriToken.constant.TradeType;
import com.mentit.mento.domain.users.entity.Users;
import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SQLDelete(sql = " update dotori_token_usage_details set is_deleted = true where user_id = ?")
public class DotoriTokenUsageDetails extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dotoriTokenUsageDetailId;

    private TradeType tradeType;

    private int tradeAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presenter_id")  // 기부하는 유저
    private Users presenter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")   // 도토리를 받는 유저
    private Users receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dotori_token_id")
    private DotoriToken dotoriToken;

    @OneToMany(mappedBy = "dotoriTokenUsageDetail")
    @Builder.Default
    private List<Board> boards = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = Boolean.FALSE;


}
