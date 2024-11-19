package com.mentit.mento.domain.comment.entity;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.global.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class Comment extends BaseEntity {

    private Long commentId;

    private Board board;

    private Users writer;

    private String comment;

}
