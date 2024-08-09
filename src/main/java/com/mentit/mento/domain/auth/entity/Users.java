package com.mentit.mento.domain.auth.entity;

import com.mentit.mento.domain.auth.constant.AccountStatus;
import com.mentit.mento.domain.auth.constant.AuthType;
import com.mentit.mento.domain.auth.constant.UserGender;
import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql=" update users set is_deleted = true where id = ?")
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthType authType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserGender userGender;

    @Column(nullable = false)
    private int birthyear;

    @Column(nullable = false)
    private int birthday;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = Boolean.FALSE;

    @Column(nullable = false)
    @Enumerated
    @Builder.Default
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Collections.singletonList(new SimpleGrantedAuthority(this.authType.name()));
    }
}
