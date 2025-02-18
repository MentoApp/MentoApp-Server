package com.mentit.mento.domain.auth.service;

import com.mentit.mento.domain.auth.dto.SocialAccountInfoDto;
import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.service.port.UserRepository;
import com.mentit.mento.global.authToken.entity.SocialAccessToken;
import com.mentit.mento.global.authToken.repository.SocialAccessTokenRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.jwt.service.JwtUtil;
import com.mentit.mento.global.oauth.service.OAuth2RevokeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final SocialAccessTokenRepository socialAccessTokenRepository;
    private final OAuth2RevokeService oAuth2RevokeService;
    private final JwtUtil jwtUtil;

    @Transactional
    public void deleteSocialMember(String BearerToken) {
        String accessToken = jwtUtil.resolveToken(BearerToken);
        log.info("accessToken: {}", accessToken);
        Long userId = jwtUtil.getUserIdFromToken(accessToken);
        UsersEntity findUser = getUsers(userId);

        socialAccessTokenRepository.findByUser(findUser).ifPresent(
                token -> {
                    String socialAccessToken = token.getSocialAccessToken();
                    revokeSocialAccessToken(findUser, socialAccessToken);
                    socialAccessTokenRepository.delete(token);
                }
        );
        userRepository.delete(findUser);
    }

    @Transactional
    public void logout(Long userId) {
        UsersEntity usersEntity = getUsers(userId);
        usersEntity.setTokenIssuedAt(null);
    }

    private void revokeSocialAccessToken(UsersEntity findUser, String socialAccessToken) {
        switch (findUser.getAuthType()) {
            case MEMBER_KAKAO -> oAuth2RevokeService.revokeKakao(socialAccessToken);
            case MEMBER_NAVER -> oAuth2RevokeService.revokeNaver(socialAccessToken);
            default -> {
            } // 다른 타입의 회원은 소셜 토큰 철회가 필요 없음
        }
    }

    private UsersEntity getUsers(Long uuid) {
        return userRepository.findById(uuid).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }

    /**
     * 들어오는 roleType이 kakao면 naver가 있는지 확인후 있으면 중복로그인 예외던지기, 아니면 정보 업데이트나 가입하기
     *
     * @param socialAccountInfoDto
     * @return
     */
    @Transactional
    public UsersEntity getOrCreateUserInfo(SocialAccountInfoDto socialAccountInfoDto) {
        if (socialAccountInfoDto.getName() != null && socialAccountInfoDto.getPhoneNumber() != null
                && socialAccountInfoDto.getBirthDay() != null && socialAccountInfoDto.getBirthYear() != null
        ) {
            UsersEntity usersEntity = userRepository.findByNameAndPhoneNumberAndBirthDayAndBirthYear(
                    socialAccountInfoDto.getName(),
                    socialAccountInfoDto.getPhoneNumber(),
                    socialAccountInfoDto.getBirthDay(),
                    socialAccountInfoDto.getBirthYear()).orElse(null);
            AuthType authType = AuthType.of(socialAccountInfoDto.getAuthType());

            //가입시 들어온 유저의 롤타입과 유저의 롤타입이 다를때(다른 계정으로 로그인 시도시)
            if (usersEntity!= null && !usersEntity.isNewUser() && !usersEntity.getAuthType().equals(authType)) {
                if (usersEntity.getAuthType() == AuthType.MEMBER_NAVER) {
                    throw new MemberException(ExceptionCode.ALREADY_ENROLLED_ACCOUNT_KAKAO);
                } else {
                    throw new MemberException(ExceptionCode.ALREADY_ENROLLED_ACCOUNT_NAVER);
                }
            }
        }

        //정상 로직

        // 기존 사용자를 이메일로 검색
        return userRepository.findByEmail(socialAccountInfoDto.getEmail()).map(userEntity -> {
            // 사용자가 존재할 경우 소셜 액세스 토큰 업데이트
            socialAccessTokenRepository.findByUser(userEntity)
                    .ifPresent(socialAccessToken ->
                            socialAccessToken.updateSocialAccessToken(socialAccountInfoDto.getSocialAccessToken())
                    );
            return userEntity; // 기존 사용자 반환
        }).orElseGet(() -> {
            // 사용자가 없을 경우 새 사용자 생성
            UsersEntity createdUser = UsersEntity.to(socialAccountInfoDto);

            // 새 사용자와 연관된 소셜 액세스 토큰 저장
            socialAccessTokenRepository.save(
                    SocialAccessToken.of(socialAccountInfoDto.getSocialAccessToken(), createdUser)
            );
            // 새 사용자 저장
            createdUser = userRepository.save(createdUser);
            return createdUser; // 새 사용자 반환
        });
    }
}
