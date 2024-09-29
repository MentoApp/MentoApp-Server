package com.mentit.mento.domain.users.entity;

import com.mentit.mento.domain.users.constant.MyStatusTags;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MyStatusTagsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myStatusTagId;

    @Enumerated(EnumType.STRING)
    private MyStatusTags myStatusTag; // 상태 태그

    @ManyToOne // UserStatusTag와의 관계 설정
    @JoinColumn(name = "user_status_tag_id")
    private UserStatusTag userStatusTag; // UserStatusTag 참조
}
