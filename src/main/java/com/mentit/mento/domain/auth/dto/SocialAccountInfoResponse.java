package com.mentit.mento.domain.auth.dto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "소셜 계정 정보 응답")
public class SocialAccountInfoResponse {

    @Schema(description = "새로운 사용자 여부", example = "true")
    private boolean isNewUser;

    public SocialAccountInfoResponse(boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }
}
