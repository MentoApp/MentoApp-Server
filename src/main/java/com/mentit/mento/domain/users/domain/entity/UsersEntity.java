package com.mentit.mento.domain.users.domain.entity;

import com.mentit.mento.domain.auth.dto.SocialAccountInfoDto;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.comment.entity.CommentEntity;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetailsEntity;
import com.mentit.mento.domain.users.constant.AccountStatus;
import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.constant.UserGenderEnum;
import com.mentit.mento.domain.users.constant.UserJobEnum;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.global.BaseEntity;
import com.mentit.mento.global.security.util.PasswordUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = " update users set is_deleted = true where user_id = ?")
@SQLRestriction("is_deleted=false")
public class UsersEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type")
    private AuthType authType;

    @Builder.Default
    @Column(name = "is_new_user", nullable = false)
    private boolean isNewUser = Boolean.TRUE;

    @Enumerated(EnumType.STRING)
    @Column(name = "job")
    private UserJobEnum job;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private UserGenderEnum gender;

    @Column(name = "birth_year")
    private String birthYear;

    @Column(name = "birth_day")
    private String birthDay;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = Boolean.FALSE;

    @Column(name = "simple_introduce")
    private String simpleIntroduce;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @OneToOne(mappedBy = "usersEntity", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private DotoriTokenEntity dotoriTokenEntity;

    @OneToMany(mappedBy = "presenter")
    @Builder.Default
    private List<DotoriTokenUsageDetailsEntity> presentedDotoriTokens = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    @Builder.Default
    private List<DotoriTokenUsageDetailsEntity> receivedDotoriTokens = new ArrayList<>();

    @OneToOne(mappedBy = "usersEntity")
    private UserStatusTagEntity userStatusTagEntity;

    @OneToMany(mappedBy = "usersEntity", fetch = FetchType.EAGER)
    private List<BoardKeywordEntity> boardKeywords;

    @OneToMany(mappedBy = "writer")
    private List<CommentEntity> commentEntity;

    @OneToMany(mappedBy = "writer")
    private List<BoardEntity> boardEntities;

    public Users to() {
        return Users.builder()
                .userId(this.userId)
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .authType(this.authType)
                .isNewUser(this.isNewUser)
                .job(this.job)
                .gender(this.gender)
                .birthYear(this.birthYear)
                .birthDay(this.birthDay)
                .phoneNumber(this.phoneNumber)
                .isDeleted(this.isDeleted)
                .simpleIntroduce(this.simpleIntroduce)
                .profileImage(this.profileImage)
                .accountStatus(this.accountStatus)
                .build();
    }

    public static UsersEntity to(SocialAccountInfoDto socialAccountInfoDto) {
        return UsersEntity.builder()
                .email(socialAccountInfoDto.getEmail())
                .name(socialAccountInfoDto.getName())
                .authType(AuthType.of(socialAccountInfoDto.getAuthType()))
                .gender(UserGenderEnum.valueOf(socialAccountInfoDto.getGender()))
                .birthYear(socialAccountInfoDto.getBirthYear())
                .birthDay(socialAccountInfoDto.getBirthDay())
                .phoneNumber(socialAccountInfoDto.getPhoneNumber())
                .isNewUser(true)
                .password(PasswordUtil.generateRandomPassword())
                .build();
    }



    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.authType.name()));
    }

}