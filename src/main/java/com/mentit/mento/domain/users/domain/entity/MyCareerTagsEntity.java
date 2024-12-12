package com.mentit.mento.domain.users.domain.entity;

import com.mentit.mento.domain.users.constant.MyCareerTagsEnum;
import com.mentit.mento.domain.users.domain.MyCareerTags;
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
    @Column(name = "my_career_tags_id")
    private Long myCareerTagsId;

    @Enumerated(EnumType.STRING)
    @Column(name = "my_career_tags_enum")
    private MyCareerTagsEnum myCareerTagsEnum;

    @OneToOne(mappedBy = "myCareerTags")
    private UserStatusTagEntity userStatusTagEntity;

    public MyCareerTags to() {
        return MyCareerTags.builder()
                .myCareerTagsId(myCareerTagsId)
                .myCareerTagsEnum(myCareerTagsEnum)
                .userStatusTagEntity(userStatusTagEntity)
                .build();
    }
}