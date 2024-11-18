package com.mentit.mento.domain.board.domain;

import com.mentit.mento.domain.board.constant.BoardTypeEnum;
import com.mentit.mento.domain.board.domain.entity.BoardFilesEntity;
import com.mentit.mento.domain.board.domain.entity.BoardKeywordForCreatingEntity;
import com.mentit.mento.domain.comment.entity.Comment;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetails;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.global.BaseEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class Board extends BaseEntity {

    private Long boardId;
    private BoardTypeEnum boardTypeEnum;
    private String title;
    private UsersEntity writer;
    private String content;
    private Long viewCount;
    private DotoriTokenUsageDetails dotoriTokenUsageDetail;
    @Builder.Default
    private List<BoardKeywordForCreating> boardKeywordForCreatings = new ArrayList<>();
    @Builder.Default
    private boolean isDeleted = Boolean.FALSE;
    @Builder.Default
    private List<BoardFiles> boardFileEntities = new ArrayList<>();
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();


}
