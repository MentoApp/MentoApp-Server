package com.mentit.mento.domain.users.domain.entity;

import com.mentit.mento.domain.users.constant.CorporateFormEnum;
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
@Table(name = "UserStatusTag")
public class UserStatusTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_status_tag_id")
    private Long userStatusTagId;

    @Enumerated(EnumType.STRING)
    @Column(name = "corporate_form_enum")
    private CorporateFormEnum corporateFormEnum;

    @OneToMany(mappedBy = "userStatusTagEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MyStatusTagsEntity> myStatus = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private MyCareerTagsEntity myCareerTags;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_user_id")
    private UsersEntity usersEntity;

    public UserStatusTag to() {
        return UserStatusTag.builder()
                .userStatusTagId(userStatusTagId)
                .myCareerTags(this.myCareerTags != null ? this.myCareerTags.to() : null)
                .myStatus(myStatus)
                .users(usersEntity != null ? usersEntity.to() : null)
                .build();
    }
}