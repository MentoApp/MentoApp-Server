package com.mentit.mento.mentoApp.domain.token.entity;

import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "token_levels")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenLevel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_tokens", nullable = false)
    private Integer minTokens;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenLevelType level;

    public enum TokenLevelType {
        EXPERT, PRO, INTERMEDIATE, BEGINNER
    }
}
