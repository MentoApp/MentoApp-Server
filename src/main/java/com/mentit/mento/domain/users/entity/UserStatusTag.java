package com.mentit.mento.domain.users.entity;

import com.mentit.mento.domain.users.constant.*;
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
public class UserStatusTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userStatusTagId;

    private String personalHistory; // 연차 (단일 선택)

    @Enumerated(EnumType.STRING)
    private CorporateForm corporateForm; // 회사형태 (단일 선택)

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_status_tag_id")
    @Builder.Default
    private List<BaseTagEntity> baseTags = new ArrayList<>(); // 복수 선택 가능한 태그 (베이스)

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_status_tag_id")
    @Builder.Default
    private List<MyStatusTagsEntity> myStatus = new ArrayList<>(); // 복수 선택 가능한 태그 카테고리

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_status_tag_id")
    @Builder.Default
    private List<CurrentJobStatusEntity> currentJobStatus = new ArrayList<>(); // 현재 직업 상태

    @OneToOne
    @JoinColumn(name = "users_user_id")
    private Users users;

}
