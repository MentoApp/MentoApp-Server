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
    @Column(name = "my_status_tag_id")
    private Long myStatusTagId;

    @Enumerated(EnumType.STRING)
    @Column(name = "my_status_tag_enum")
    private MyStatusTagsEnum myStatusTagEnum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_status_tag_id")
    private UserStatusTagEntity userStatusTagEntity;

    public MyStatusTags to() {
        return MyStatusTags.builder()
                .myStatusTagId(myStatusTagId)
                .userStatusTag(userStatusTagEntity)
                .myStatusTag(myStatusTagEnum)
                .build();
    }
}