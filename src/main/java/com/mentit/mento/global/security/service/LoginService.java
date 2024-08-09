package com.mentit.mento.global.security.service;

import com.mentit.mento.domain.auth.entity.Users;
import com.mentit.mento.domain.auth.repository.UserRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users findUser = userRepository.findByEmail(username).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );

        return new CustomUserDetail(
                findUser.getEmail(),
                findUser.getPassword(),
                findUser.getId(),
                findUser.getAuthorities()
        );
    }
}
