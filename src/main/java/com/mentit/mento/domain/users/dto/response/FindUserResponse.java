package com.mentit.mento.domain.users.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FindUserResponse {
    private Long id;
    private String name;
    private String nickname;
    private String profileImage;
    private String phoneNumber;
    private String simpleIntroduce;

    private int dotoriTokenAmount;

    private List<String> boardKeywordList;

    private String personalHistory;

    private List<String> baseTags;

    private List<String> myStatus;

    private List<String> currentJobStatus;

    private String corporateForm;

    private String userJob;


}
