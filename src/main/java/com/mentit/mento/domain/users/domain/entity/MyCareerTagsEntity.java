package com.mentit.mento.domain.users.domain.entity;

import com.mentit.mento.domain.users.constant.MyCareerTagsEnum;
import com.mentit.mento.domain.users.domain.MyCareerTags;
import com.mentit.mento.domain.users.domain.UserStatusTag;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "myCareerTags")
public class MyCareerTagsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myCareerTagsId;

    @Enumerated(EnumType.STRING)
    private MyCareerTagsEnum myCareerTagsEnum; // 상태 태그

    @OneToOne(mappedBy = "myCareerTags") // 주인을 명확히 설정
    private UserStatusTagEntity userStatusTagEntity; // UserStatusTag 참조



    public MyCareerTags to() {
        return MyCareerTags.builder()
                .myCareerTagsId(myCareerTagsId)
                .myCareerTagsEnum(myCareerTagsEnum)
                .userStatusTagEntity(userStatusTagEntity)
                .build();
    }
}
