package com.mentit.mento.domain.users.domain.entity;

import com.mentit.mento.domain.users.constant.MyCareerTagsEnum;
import com.mentit.mento.domain.users.domain.MyCareerTags;
import com.mentit.mento.domain.users.domain.UserStatusTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.lang.model.element.Name;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "myCareerTags")
public class MyCareerTagsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myCareerTagsId;

    @Enumerated(EnumType.STRING)
    private MyCareerTagsEnum myCareerTagsEnum; // 상태 태그

    @OneToOne(mappedBy = "myCareerTags") // 주인을 명확히 설정
    private UserStatusTagEntity userStatusTagEntity; // UserStatusTag 참조

    public static MyCareerTagsEntity from(MyCareerTags myCareerTags) {
        return MyCareerTagsEntity.builder()
                .myCareerTagsEnum(myCareerTags.getMyCareerTagsEnum())
                .build();
    }

    public static MyCareerTagsEntity from(MyCareerTags myCareerTags, UserStatusTag userStatusTag) {
        return MyCareerTagsEntity.builder()
                .myCareerTagsEnum(myCareerTags.getMyCareerTagsEnum())
                .userStatusTagEntity(UserStatusTagEntity.from(userStatusTag))
                .build();
    }

    public MyCareerTags to() {
        return MyCareerTags.builder()
                .myCareerTagsId(myCareerTagsId)
                .myCareerTagsEnum(myCareerTagsEnum)
                .userStatusTagEntity(userStatusTagEntity)
                .build();
    }
}
