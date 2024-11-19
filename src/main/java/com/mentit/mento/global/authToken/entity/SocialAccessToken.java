package com.mentit.mento.global.authToken.entity;

import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Slf4j
public class SocialAccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String socialAccessToken;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_id")
    private UsersEntity user;

    public static SocialAccessToken of(String socialAccessToken, UsersEntity user) {
        return SocialAccessToken.builder()
                .socialAccessToken(socialAccessToken)
                .user(user)
                .build();
    }


    public void updateSocialAccessToken(String socialAccessToken) {
        this.socialAccessToken = socialAccessToken;
    }
}
