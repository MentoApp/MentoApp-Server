package com.mentit.mento.domain.users.dto.request;

import com.mentit.mento.domain.users.constant.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ModifyUserRequest {

    @Schema(description = "사용자의 닉네임", example = "홍길동")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영어, 숫자만 가능하며 공백과 특수문자는 사용할 수 없습니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;

    @Schema(description = "사용자의 직무", example = "프론트엔드 개발")
    private UserJob job;

    @Schema(description = "사용자의 연차 정보", example = "신입~2년")
    private MyCareerTags personalHistory;

    @Schema(description = "회사의 형태", example = "스타트업")
    private CorporateForm corporateForm;

    @Schema(description = "사용자의 베이스 태그 리스트", example = "[\"부트캠프 수료자\", \"비전공자\"]")
    private List<BaseTag> baseTags;

    @Schema(description = "사용자의 현재 상태 태그 리스트", example = "[\"프로이직러\", \"대학생\"]")
    private List<MyStatusTags> myStatus;

    @Schema(description = "사용자의 현재 직무 상태 리스트", example = "[\"헬스케어\", \"모빌리티\"]")
    private List<CurrentJobStatus> currentJobStatus;

    @Schema(description = "간단한 자기 소개", example = "안녕하세요, 백엔드 개발자 김영한입니다.")
    private String simpleIntroduce;

    @Schema(description = "사용자가 선택한 게시판 키워드 리스트", example = "[\"트렌드\", \"회사 생활\"]")
    private List<BoardKeyword> boardKeywords;

}
