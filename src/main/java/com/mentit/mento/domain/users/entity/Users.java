package com.mentit.mento.domain.users.entity;

import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.comment.entity.Comment;
import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetails;
import com.mentit.mento.domain.users.constant.*;
import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = " update Users set is_deleted = true where user_id = ?")
public class Users extends BaseEntity {

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
    private UserJob job;

    @Enumerated(EnumType.STRING)
    private UserGender gender;

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

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.authType.name()));
    }

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private DotoriToken dotoriToken;

    @OneToMany(mappedBy = "presenter")
    @Builder.Default
    private List<DotoriTokenUsageDetails> presentedDotoriTokens = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    @Builder.Default
    private List<DotoriTokenUsageDetails> receivedDotoriTokens = new ArrayList<>();

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatusTag userStatusTag;

    @OneToMany(mappedBy = "users", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardKeywordEntity> boardKeywords;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comment;

    @OneToMany(mappedBy = "writer",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards;

}
