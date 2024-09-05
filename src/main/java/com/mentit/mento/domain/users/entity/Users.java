package com.mentit.mento.domain.users.entity;

import com.mentit.mento.domain.users.constant.AccountStatus;
import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.constant.UserGender;
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
import java.util.List;

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

    private String name;

    private String email;

    private String password;

    private String nickname;

    private String organization;

    private String job;

    private String preferredJob;

    @Enumerated(EnumType.STRING)
    private AuthType authType;

    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @Column(nullable = false)
    private int birthyear;

    @Column(nullable = false)
    private int birthday;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Role> role;

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
