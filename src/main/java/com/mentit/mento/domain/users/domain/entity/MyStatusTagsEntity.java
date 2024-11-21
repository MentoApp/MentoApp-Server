package com.mentit.mento.domain.users.domain.entity;

import com.mentit.mento.domain.users.constant.MyStatusTagsEnum;
import com.mentit.mento.domain.users.domain.MyStatusTags;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "myStatusTags")
public class MyStatusTagsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myStatusTagId;

    @Enumerated(EnumType.STRING)
    private MyStatusTagsEnum myStatusTagEnum; // 상태 태그

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_status_tag_id")
    private UserStatusTagEntity userStatusTagEntity; // 연관 관계를 설정하는 필드

    public MyStatusTags to() {
        return MyStatusTags.builder()
                .myStatusTagId(myStatusTagId)
                .userStatusTag(userStatusTagEntity)
                .myStatusTag(myStatusTagEnum)
                .build();
    }


}
