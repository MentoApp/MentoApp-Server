package com.mentit.mento.domain.auth.constant;

public enum AuthType {
    MEMBER_KAKAO, MEMBER_NAVER;

    public static AuthType of(String provider) {
        return switch (provider.toLowerCase()) {
            case "kakao" -> MEMBER_KAKAO;
            case "naver" -> MEMBER_NAVER;
            default -> throw new IllegalStateException("Unexpected value: " + provider.toLowerCase());
        };
    }
}
