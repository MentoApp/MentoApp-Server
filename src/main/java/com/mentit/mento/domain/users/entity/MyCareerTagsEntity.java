package com.mentit.mento.domain.users.entity;

import com.mentit.mento.domain.users.constant.MyCareerTags;
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
public class MyCareerTagsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myCareerTagsId;

    @Enumerated(EnumType.STRING)
    private MyCareerTags myCareerTags; // 상태 태그

    @OneToOne(mappedBy = "myCareerTags") // 주인을 명확히 설정
    private UserStatusTag userStatusTag; // UserStatusTag 참조
}
