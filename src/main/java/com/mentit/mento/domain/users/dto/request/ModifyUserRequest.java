package com.mentit.mento.domain.users.dto.request;

import com.mentit.mento.domain.users.constant.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ModifyUserRequest {

    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영어, 숫자만 가능하며 공백과 특수문자는 사용할 수 없습니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;

    private UserJob job;

    // UserStatusTag 관련 필드 추가
    private String personalHistory; // 연차 (단일 선택)
    private CorporateForm corporateForm; // 회사 형태 (단일 선택)
    private List<BaseTag> baseTags; // 복수 선택 가능한 베이스 태그
    private List<MyStatusTags> myStatus; // 복수 선택 가능한 나의 상태 태그
    private List<CurrentJobStatus> currentJobStatus; // 복수 선택 가능한 현재 직무 상태

    private String simpleIntroduce;


}
