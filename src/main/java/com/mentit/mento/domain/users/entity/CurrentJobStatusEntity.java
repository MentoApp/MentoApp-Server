package com.mentit.mento.domain.users.entity;

import com.mentit.mento.domain.users.constant.CurrentJobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CurrentJobStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CurrentJobStatus currentJobStatus; // Enum 타입 필드

    @ManyToOne
    @JoinColumn(name = "user_status_tag_id")
    private UserStatusTag userStatusTag; // 연관된 UserStatusTag
}
