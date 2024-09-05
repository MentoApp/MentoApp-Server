package com.mentit.mento.domain.mentor.entity;

import com.mentit.mento.domain.users.entity.Users;
import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mentoring_requests")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentoringRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private Users mentee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Users mentor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    public enum RequestStatus {
        PENDING, ACCEPTED, REJECTED
    }
}
