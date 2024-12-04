package com.mentit.mento.domain.users.domain.entity;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.comment.entity.CommentEntity;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenEntity;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetailsEntity;
import com.mentit.mento.domain.users.constant.*;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = " update Users set is_deleted = true where user_id = ?")
@SQLRestriction("is_deleted=false")
public class UsersEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
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

    @Builder.Default
    private boolean isNewUser= Boolean.TRUE;

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

}
