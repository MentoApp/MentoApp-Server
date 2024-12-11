package com.mentit.mento.global.oauth.service;

import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.constant.UserGenderEnum;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.service.port.UserRepository;
import com.mentit.mento.global.authToken.entity.SocialAccessToken;
import com.mentit.mento.global.authToken.repository.SocialAccessTokenRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.oauth.dto.OAuthAttributes;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import com.mentit.mento.global.security.util.PasswordUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import java.util.Optional;
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
        UserGenderEnum gender = memberAttribute.get("gender") != null ? UserGenderEnum.valueOf(((String) memberAttribute.get("gender")).toUpperCase()) : null;
        String birthDay = memberAttribute.get("birthday") != null ? (String) memberAttribute.get("birthday") : null;
        String birthYear = memberAttribute.get("birthyear") != null ? (String) memberAttribute.get("birthyear") : null;
        String phoneNumber = memberAttribute.get("phoneNumber") != null ? (String) memberAttribute.get("phoneNumber") : null;

        AtomicBoolean isNewUser = new AtomicBoolean(false);
        
        // 이메일로 기존 유저 찾기 전에 로그 추가
        log.info("Searching for user with email: {}", email);
        
        Optional<UsersEntity> existingUserOptional = userRepository.findByEmail(email);
        existingUserOptional.ifPresent(existingUser -> 
            log.info("Found existing user with ID: {}", existingUser.getUserId())
        );

        UsersEntity user= null;
        if (existingUserOptional.isPresent()) {
            user = existingUserOptional.get();
            log.info("Using existing user with ID: {}", user.getUserId());
            
            // 소셜 액세스 토큰 업데이트

            updateSocialAccessToken(user, socialAccessToken);
        } else {
            user = createNewUser(email, name, nickname, profileImage, phoneNumber,
                    authType, gender, birthDay, birthYear);
            log.info("Created new user with ID: {}", user.getUserId());
        }

        return createCustomUserDetail(user, authType, memberAttribute, isNewUser.get());
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateSocialAccessToken(UsersEntity userEntity, String socialAccessToken) {
        socialAccessTokenRepository.findByUser(userEntity).ifPresentOrElse(
                existingToken -> {
                    log.info("Updating social access token for user: {}", userEntity.getUserId());
                    existingToken.updateSocialAccessToken(socialAccessToken);

                    // 영속 상태 보장
                    entityManager.merge(existingToken);
                },
                () -> {
                    log.info("Creating new social access token for user: {}", userEntity.getUserId());
                    SocialAccessToken newToken = SocialAccessToken.of(socialAccessToken, userEntity);

                    // 영속 상태 보장
                    entityManager.merge(newToken);
                }
        );
    }

    private UsersEntity createNewUser(String email, String name, String nickname, String profileImage,
                              String phoneNumber, AuthType authType, UserGenderEnum gender,
                              String birthDay, String birthYear) {
        String encodedPassword = new BCryptPasswordEncoder()
                .encode(PasswordUtil.generateRandomPassword());

        UsersEntity newUser = UsersEntity.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .profileImage(profileImage)
                .phoneNumber(phoneNumber)
                .authType(authType)
                .gender(gender)
                .birthDay(birthDay)
                .birthYear(birthYear)
                .password(encodedPassword)
                .isNewUser(true)
                .build();

        return userRepository.save(newUser);
    }

    private CustomUserDetail createCustomUserDetail(UsersEntity user, AuthType authType,
                                                  Map<String, Object> memberAttribute,
                                                  boolean isNewUser) {
        CustomUserDetail customUserDetail = new CustomUserDetail(
                user,
                Collections.singleton(new SimpleGrantedAuthority(authType.name())),
                memberAttribute);
        customUserDetail.setIsNewUser(isNewUser);
        return customUserDetail;
    }

}
