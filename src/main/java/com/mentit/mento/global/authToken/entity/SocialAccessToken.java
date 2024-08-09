package com.mentit.mento.global.authToken.entity;

import com.mentit.mento.domain.auth.entity.Users;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Users user;

    public static SocialAccessToken of(String socialAccessToken, Users user) {
        return SocialAccessToken.builder()
                .socialAccessToken(socialAccessToken)
                .user(user)
                .build();
    }


    public void updateSocialAccessToken(String socialAccessToken) {
        this.socialAccessToken = socialAccessToken;
    }
}
