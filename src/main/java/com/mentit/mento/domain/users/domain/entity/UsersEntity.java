package com.mentit.mento.domain.users.domain.entity;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.comment.entity.CommentEntity;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetailsEntity;
import com.mentit.mento.domain.users.constant.*;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = " update Users set is_deleted = true where user_id = ?")
public class UsersEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private AuthType authType;

    private boolean isNewUser;

    @Enumerated(EnumType.STRING)
    private UserJobEnum job;

    @Enumerated(EnumType.STRING)
    private UserGenderEnum gender;

    @Column(nullable = false)
    private String birthYear;

    @Column(nullable = false)
    private String birthDay;

    private String phoneNumber;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = Boolean.FALSE;

    private String simpleIntroduce;

    private String profileImage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    public static UsersEntity from(Users user) {
        return UsersEntity.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .authType(user.getAuthType())
                .isNewUser(user.isNewUser())
                .job(user.getJob())
                .gender(user.getGender())
                .birthYear(user.getBirthYear())
                .birthDay(user.getBirthDay())
                .phoneNumber(user.getPhoneNumber())
                .isDeleted(user.isDeleted())
                .simpleIntroduce(user.getSimpleIntroduce())
                .profileImage(user.getProfileImage())
                .accountStatus(user.getAccountStatus())
                .build();
    }


    public Users to(){
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

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.authType.name()));
    }

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private DotoriTokenEntity dotoriTokenEntity;

    @OneToMany(mappedBy = "presenter")
    @Builder.Default
    private List<DotoriTokenUsageDetailsEntity> presentedDotoriTokens = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    @Builder.Default
    private List<DotoriTokenUsageDetailsEntity> receivedDotoriTokens = new ArrayList<>();

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatusTagEntity userStatusTagEntity;

    @OneToMany(mappedBy = "users", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardKeywordEntity> boardKeywords;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> commentEntity;

    @OneToMany(mappedBy = "writer",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardEntity> boardEntities;

}
