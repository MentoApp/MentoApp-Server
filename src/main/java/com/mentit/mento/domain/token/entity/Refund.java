package com.mentit.mento.domain.token.entity;

import com.mentit.mento.domain.token.entity.Token;
import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refunds")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Refund extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", nullable = false)
    private Token token;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "refund_reason", nullable = false)
    private String refundReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status;

    public enum RefundStatus {
        PENDING, COMPLETED, REJECTED
    }
}
