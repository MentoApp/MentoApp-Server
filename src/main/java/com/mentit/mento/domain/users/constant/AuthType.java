package com.mentit.mento.domain.users.constant;

public enum AuthType {
    MEMBER_KAKAO("카카오"), MEMBER_NAVER("네이버");

    private final String koreanValue;

    AuthType(String koreanValue) {
        this.koreanValue = koreanValue;
    }

    public static AuthType of(String provider) {
        return switch (provider.toLowerCase()) {
            case "kakao" -> MEMBER_KAKAO;
            case "naver" -> MEMBER_NAVER;
            default -> throw new IllegalStateException("Unexpected value: " + provider.toLowerCase());
        };
    }

    public static String fromEnumValue(AuthType authType) {
        for (AuthType form : values()) {
            if (form.equals(authType)) {
                return form.koreanValue;
            }
        }
        throw new IllegalArgumentException("잘못된 플랫폼: " + authType);
    }
}
