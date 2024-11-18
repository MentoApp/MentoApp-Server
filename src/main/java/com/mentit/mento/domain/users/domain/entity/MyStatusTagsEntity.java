package com.mentit.mento.domain.users.domain.entity;

import com.mentit.mento.domain.users.constant.MyStatusTagsEnum;
import com.mentit.mento.domain.users.domain.MyStatusTags;
import com.mentit.mento.domain.users.domain.UserStatusTag;
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
    private MyStatusTagsEnum myStatusTag; // 상태 태그

    @ManyToOne // UserStatusTag와의 관계 설정
    @JoinColumn(name = "user_status_tag_id")
    private UserStatusTagEntity userStatusTagEntity; // UserStatusTag 참조

    public MyStatusTags to() {
        return MyStatusTags.builder()
                .myStatusTagId(myStatusTagId)
                .userStatusTag(userStatusTagEntity)
                .myStatusTag(myStatusTag)
                .build();
    }

    public static MyStatusTagsEntity from(MyStatusTags myStatus, UserStatusTag userStatusTag) {
        return MyStatusTagsEntity.builder()
                .myStatusTag(myStatus.getMyStatusTag())
                .userStatusTagEntity(UserStatusTag.from(userStatusTag))
                .build();

    }

    public static MyStatusTagsEntity from(MyStatusTags myStatus) {
        return MyStatusTagsEntity.builder()
                .myStatusTag(myStatus.getMyStatusTag())
                .build();
    }
}
