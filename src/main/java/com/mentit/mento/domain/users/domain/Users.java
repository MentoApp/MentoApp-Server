package com.mentit.mento.domain.users.domain;

import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.comment.entity.Comment;
import com.mentit.mento.domain.dotoriToken.entity.DotoriToken;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetails;
import com.mentit.mento.domain.users.constant.AccountStatus;
import com.mentit.mento.domain.users.constant.AuthType;
import com.mentit.mento.domain.users.constant.UserGenderEnum;
import com.mentit.mento.domain.users.constant.UserJobEnum;
import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Getter
public class Users {

    private Long userId;
    private String name;
    private String email;
    private String password;
    private String nickname;
    private AuthType authType;
    private boolean isNewUser;
    private UserJobEnum job;
    private UserGenderEnum gender;
    private String birthYear;
    private String birthDay;
    private String phoneNumber;
    private boolean isDeleted = Boolean.FALSE;
    private String simpleIntroduce;
    private String profileImage;
    private AccountStatus accountStatus = AccountStatus.ACTIVE;
    private DotoriToken dotoriToken;
    private List<DotoriTokenUsageDetails> presentedDotoriTokens = new ArrayList<>();
    private List<DotoriTokenUsageDetails> receivedDotoriTokens = new ArrayList<>();
    private UserStatusTagEntity userStatusTagEntity;
    private List<BoardKeywordEntity> boardKeywords;
    private List<Comment> comment;
    private List<Board> boards;
}
