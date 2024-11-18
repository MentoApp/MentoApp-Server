package com.mentit.mento.domain.users.domain.entity;

import com.mentit.mento.domain.users.constant.*;
import com.mentit.mento.domain.users.domain.UserStatusTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserStatusTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userStatusTagId;

    @Enumerated(EnumType.STRING)
    private CorporateFormEnum corporateFormEnum; // 회사형태 (단일 선택)

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_status_tag_id")
    @Builder.Default
    private List<MyStatusTagsEntity> myStatus = new ArrayList<>(); // 복수 선택 가능한 태그 카테고리


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private MyCareerTagsEntity myCareerTags; // 연차

    @OneToOne
    @JoinColumn(name = "users_user_id")
    private UsersEntity usersEntity;

    public static UserStatusTagEntity from(UserStatusTag userStatusTag) {
        return UserStatusTagEntity.builder()
                .corporateFormEnum(userStatusTag.getCorporateFormEnum())
                .myCareerTags(MyCareerTagsEntity.from(userStatusTag.getMyCareerTags()))
                .myStatus(userStatusTag.getMyStatus().stream().map(MyStatusTagsEntity::from).toList())
                .usersEntity(UsersEntity.from(userStatusTag.getUsersEntity()))
                .build();
    }

    public UserStatusTag to() {
        return UserStatusTag.builder()
                .myCareerTags(this.myCareerTags.to())
                .userStatusTagId(this.userStatusTagId)
                .myStatus(myStatus.stream().map(MyStatusTagsEntity::to).toList())
                .usersEntity(this.usersEntity.to())
                .build();
    }
}