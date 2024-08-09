package com.mentit.mento.domain.mentor.entity;

import com.mentit.mento.domain.auth.entity.Users;
import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mentors")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mentor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentorLevel level;

    public enum MentorLevel {
        EXPERT, PRO, INTERMEDIATE, BEGINNER
    }
}
