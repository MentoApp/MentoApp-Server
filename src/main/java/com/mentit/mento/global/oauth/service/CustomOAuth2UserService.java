package com.mentit.mento.global.oauth.service;

import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.constant.UserGender;
import com.mentit.mento.domain.users.entity.Users;
import com.mentit.mento.domain.users.repository.UserRepository;
import com.mentit.mento.global.authToken.entity.SocialAccessToken;
import com.mentit.mento.global.authToken.repository.SocialAccessTokenRepository;
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
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final SocialAccessTokenRepository socialAccessTokenRepository;

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
        String birthDay = memberAttribute.get("birthday") != null ? (String) memberAttribute.get("birthday") : null;
        String birthYear = memberAttribute.get("birthyear") != null ? (String) memberAttribute.get("birthyear") : null;
        String phoneNumber = memberAttribute.get("phoneNumber") != null ? (String) memberAttribute.get("phoneNumber") : null;

        AtomicBoolean isNewUser = new AtomicBoolean(false);
        Users user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    // SocialAccessToken 엔티티 업데이트 또는 생성 로직 수정
                    socialAccessTokenRepository.findByUser(existingUser).ifPresentOrElse(
                            existingToken -> {
                                log.info("existingToken: {}", existingToken.getSocialAccessToken());
                                existingToken.updateSocialAccessToken(socialAccessToken);
                                socialAccessTokenRepository.save(existingToken);
                            },
                            () -> socialAccessTokenRepository.save(SocialAccessToken.of(socialAccessToken, existingUser)
                            )
                    );
                    return existingUser;

                }).orElseGet(() -> {
                    Users mappedUser = Users.builder()
                            .email(email)
                            .name(name)
                            .nickname(nickname)
                            .profileImage(profileImage)
                            .phoneNumber(phoneNumber)
                            .authType(authType)
                            .gender(gender)
                            .birthDay(birthDay)
                            .birthYear(birthYear)
                            .build();
                    String tempPassword = PasswordUtil.generateRandomPassword();
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    String encodedPassword = passwordEncoder.encode(tempPassword);
                    Users newUser = mappedUser.toBuilder()
                            .password(encodedPassword)
                            .build();
                    userRepository.save(newUser);
                    socialAccessTokenRepository.save(SocialAccessToken.of(socialAccessToken, newUser)); // 새로운 Member에 대한 SocialAccessToken 저장
                    isNewUser.set(true);
                    return newUser;
                });

        CustomUserDetail customUserDetail = new CustomUserDetail(
                user,
                Collections.singleton(new SimpleGrantedAuthority(AuthType.of(registrationId).name())),
                memberAttribute);
        customUserDetail.setIsNewUser(isNewUser.get());

        return customUserDetail;
    }

}
