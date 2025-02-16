package com.mentit.mento.domain.auth.service;

import com.mentit.mento.domain.auth.dto.SocialAccountInfoDto;
import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.service.port.UserRepository;
import com.mentit.mento.global.authToken.entity.RefreshToken;
import com.mentit.mento.global.authToken.entity.SocialAccessToken;
import com.mentit.mento.global.authToken.repository.RefreshTokenRepository;
import com.mentit.mento.global.authToken.repository.SocialAccessTokenRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.jwt.dto.JwtToken;
import com.mentit.mento.global.jwt.service.JwtService;
import com.mentit.mento.global.oauth.service.OAuth2RevokeService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final SocialAccessTokenRepository socialAccessTokenRepository;
    private final OAuth2RevokeService oAuth2RevokeService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EntityManager em;

    public JwtToken reissueToken(String refreshToken) {
        return jwtService.reissueTokenByRefreshToken(refreshToken);
    }

    @Transactional
    public void deleteSocialMember(Long uuid) {
        UsersEntity findUser = getUsers(uuid);


        socialAccessTokenRepository.findByUser(findUser).ifPresent(
                accessToken -> {
                    String socialAccessToken = accessToken.getSocialAccessToken();
                    revokeSocialAccessToken(findUser, socialAccessToken);
                    socialAccessTokenRepository.delete(accessToken);
                }
        );

        userRepository.delete(findUser);
    }

    @Transactional
    public void logout(Long userId) {

        String refreshToken = getRefreshToken(userId);

        jwtService.deleteRefreshTokenDB(refreshToken);

        // 쿠키에서 refreshToken 삭제
        Cookie cookie = new Cookie("refreshToken", null); // 쿠키 값을 null로 설정
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // 쿠키의 경로 설정 (일반적으로 루트 경로로 설정)
        cookie.setMaxAge(0); // 쿠키의 유효기간을 0으로 설정하여 즉시 삭제

    }


    public String getRefreshToken(Long uuid) {
        RefreshToken refreshToken = refreshTokenRepository.getRefreshTokenByMemberId(uuid).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
        return refreshToken.getRefreshToken();
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
     * @param socialAccountInfoDto
     * @return
     */
    @Transactional
    public UsersEntity getOrCreateUserInfo(SocialAccountInfoDto socialAccountInfoDto) {
        if (socialAccountInfoDto.getName() != null && socialAccountInfoDto.getPhoneNumber() != null
                && socialAccountInfoDto.getBirthDay() != null && socialAccountInfoDto.getBirthYear() != null
        ) {
            Optional<UsersEntity> usersEntity = userRepository.findByNameAndPhoneNumberAndBirthDayAndBirthYear(
                    socialAccountInfoDto.getName(), socialAccountInfoDto.getPhoneNumber(),socialAccountInfoDto.getBirthDay(), socialAccountInfoDto.getBirthYear());
            if (usersEntity.isPresent() && !usersEntity.get().isNewUser() && !socialAccountInfoDto.getAuthType().equals(usersEntity.get().getAuthType())) {
                if (usersEntity.get().getAuthType() == AuthType.MEMBER_NAVER) {
                    throw new MemberException(ExceptionCode.ALREADY_ENROLLED_ACCOUNT_KAKAO);
                } else {
                    throw new MemberException(ExceptionCode.ALREADY_ENROLLED_ACCOUNT_NAVER);
                }
            }
        }
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
