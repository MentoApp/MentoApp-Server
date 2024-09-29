package com.mentit.mento.global.security.userDetails;

import com.mentit.mento.domain.users.entity.Users;
import com.mentit.mento.global.security.util.PasswordUtil;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Getter
public class CustomUserDetail extends org.springframework.security.core.userdetails.User implements OAuth2User {

    private final Long id; // 회원 id
    private Map<String, Object> attributes;
    private boolean isNewUser;

    public CustomUserDetail(String username, String password, Long id, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.isNewUser = true;
    }

    public CustomUserDetail(Users user, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this(user.getEmail() != null ? user.getEmail() : UUID.randomUUID().toString().substring(0, 8) + "@social.com",
                user.getPassword() != null ? user.getPassword() : PasswordUtil.generateRandomPassword(),
                user.getUserId(), authorities);
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    public void setIsNewUser(boolean newUser) {
        this.isNewUser = newUser;
    }
}