package com.mentit.mento.global.oauth.service;

import com.mentit.mento.domain.auth.constant.AuthType;
import com.mentit.mento.domain.auth.constant.UserGender;
import com.mentit.mento.domain.auth.entity.Users;
import com.mentit.mento.domain.auth.mapper.UserMapper;
import com.mentit.mento.domain.auth.repository.UserRepository;
import com.mentit.mento.global.authToken.entity.SocialAccessToken;
import com.mentit.mento.global.authToken.repository.SocialAccessTokenRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.oauth.dto.OAuthAttributes;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import com.mentit.mento.global.security.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final SocialAccessTokenRepository socialAccessTokenRepository;
    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        DefaultOAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();
        String socialAccessToken = userRequest.getAccessToken().getTokenValue();

        OAuthAttributes oAuth2Attribute = OAuthAttributes.of(registrationId, userNameAttributeName,
                oAuth2User.getAttributes(), socialAccessToken);

        Map<String, Object> memberAttribute = oAuth2Attribute.convertToMap();
        String email = (String) memberAttribute.get("email");
        AuthType authType = AuthType.of(registrationId);
        String name = (String) memberAttribute.get("name");
        String profileImage = memberAttribute.get("picture") != null ? (String) memberAttribute.get("picture") : null;
        String nickname = memberAttribute.get("nickname") != null ? (String) memberAttribute.get("nickname") : null;
        UserGender gender = memberAttribute.get("gender") != null ? UserGender.valueOf(((String) memberAttribute.get("gender")).toUpperCase()) : null;
        int birthday = memberAttribute.get("birthday") != null ? (int) memberAttribute.get("birthday") : null;
        int birthyear = memberAttribute.get("birthyear") != null ? (int) memberAttribute.get("birthyear") : null;


        // 로그 추가
        log.debug("Registration ID: {}", registrationId);
        log.debug("Auth Type: {}", authType);
        log.debug("Email: {}", email);
        Users user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    if (existingUser.getAccountStatus().toString().equals("DELETED")) {
                        throw new MemberException(ExceptionCode.MEMBER_ALREADY_WITHDRAW);
                    }
                    // SocialAccessToken 엔티티 업데이트 또는 생성 로직 수정
                    socialAccessTokenRepository.findByUser(existingUser).ifPresentOrElse(
                            existingToken -> {
                                existingToken.updateSocialAccessToken(socialAccessToken);
                                socialAccessTokenRepository.save(existingToken);
                            },
                            () -> socialAccessTokenRepository.save(SocialAccessToken.of(socialAccessToken, existingUser)
                            )
                    );
                    return existingUser;

                }).orElseGet(() -> {
                    Users mappedUser = userMapper.toEntity(email, name, profileImage, authType, nickname, gender, birthday, birthyear);
                    String tempPassword = passwordUtil.generateRandomPassword();
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    String encodedPassword = passwordEncoder.encode(tempPassword);
                    Users newUser = mappedUser.toBuilder()
                            .password(encodedPassword)
                            .build();
                    userRepository.save(newUser);
                    socialAccessTokenRepository.save(SocialAccessToken.of(socialAccessToken, newUser)); // 새로운 Member에 대한 SocialAccessToken 저장
                    return newUser;
                });

        return new CustomUserDetail(
                user,
                Collections.singleton(new SimpleGrantedAuthority(AuthType.of(registrationId).name())),
                memberAttribute);
    }

}
