package com.mentit.mento.domain.users.service;

import com.mentit.mento.domain.users.constant.RoleType;
import com.mentit.mento.domain.users.dto.request.FindUserDTO;
import com.mentit.mento.domain.users.dto.request.WorkerSignUpRequestDTO;
import com.mentit.mento.domain.users.entity.Users;
import com.mentit.mento.domain.users.repository.UserRepository;
import com.mentit.mento.global.authToken.entity.RefreshToken;
import com.mentit.mento.global.authToken.repository.RefreshTokenRepository;
import com.mentit.mento.global.authToken.repository.SocialAccessTokenRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.jwt.dto.JwtToken;
import com.mentit.mento.global.jwt.service.JwtService;
import com.mentit.mento.global.oauth.service.OAuth2RevokeService;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SocialAccessTokenRepository socialAccessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2RevokeService oAuth2RevokeService;
    private final JwtService jwtService;

    public void signUpByWorker(CustomUserDetail userDetail, WorkerSignUpRequestDTO workerSignUpRequestDTO) {
        Users findUser = getUsers(userDetail);

        Users updatedUser = findUser.toBuilder()
                .organization(workerSignUpRequestDTO.getOrganization())
                .job(workerSignUpRequestDTO.getJob())
                .build();

        userRepository.save(updatedUser);
    }

    public void signUpBySeeker(CustomUserDetail userDetail, String preferredJob) {

        Users findUser = getUsers(userDetail);

        Users updatedUser = findUser.toBuilder()
                .preferredJob(preferredJob)
                .build();

        userRepository.save(updatedUser);
    }

    private Users getUsers(CustomUserDetail userDetail) {
        return userRepository.findById(userDetail.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }

    public FindUserDTO findMyInfo(CustomUserDetail userDetail, String roleType) {
        Users findUser = getUsers(userDetail);

        return switch (RoleType.valueOf(roleType)) {
            case MENTOR -> FindUserDTO.builder()
                    .id(findUser.getId())
                    .name(findUser.getName())
                    .email(findUser.getEmail())
                    .organization(findUser.getOrganization())
                    .job(findUser.getJob())
                    .nickname(findUser.getNickname())
                    .gender(findUser.getGender())
                    .birthyear(LocalDate.ofEpochDay(findUser.getBirthyear()))
                    .birthday(LocalDate.ofEpochDay(findUser.getBirthday()))
                    .authType(findUser.getAuthType())
                    .build();
            case MENTEE -> FindUserDTO.builder()
                    .id(findUser.getId())
                    .name(findUser.getName())
                    .email(findUser.getEmail())
                    .preferredJob(findUser.getPreferredJob())
                    .nickname(findUser.getNickname())
                    .gender(findUser.getGender())
                    .birthyear(LocalDate.ofEpochDay(findUser.getBirthyear()))
                    .birthday(LocalDate.ofEpochDay(findUser.getBirthday()))
                    .authType(findUser.getAuthType())
                    .build();
        };
    }

    public void deleteSocialMember(Long uuid) {
        Users findUser = getUserById(uuid);

        socialAccessTokenRepository.findByUser(findUser).ifPresent(
                accessToken -> {
                    String socialAccessToken = accessToken.getSocialAccessToken();
                    revokeSocialAccessToken(findUser, socialAccessToken);
                    socialAccessTokenRepository.delete(accessToken);
                }
        );

        userRepository.delete(findUser);
    }

    private void revokeSocialAccessToken(Users findUser, String socialAccessToken) {
        switch (findUser.getAuthType()) {
            case MEMBER_KAKAO -> oAuth2RevokeService.revokeKakao(socialAccessToken);
            case MEMBER_NAVER -> oAuth2RevokeService.revokeNaver(socialAccessToken);
        }
    }

    public String getRefreshToken(Long id) {
        Users findUser = getUserById(id);
        RefreshToken refreshToken = refreshTokenRepository.getRefreshTokenByMemberId(id).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
        return refreshToken.getRefreshToken();
    }

    private Users getUserById(Long id) {
        refreshTokenRepository.getRefreshTokenByMemberId(id).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_REFRESH_TOKEN)
        );
        return userRepository.findById(id).
                orElseThrow(
                        () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
                );
    }

    public JwtToken reissueToken(String refreshToken) {
        return jwtService.reissueTokenByRefreshToken(refreshToken);
    }
}
