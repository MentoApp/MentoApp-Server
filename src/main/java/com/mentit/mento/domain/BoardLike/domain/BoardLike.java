package com.mentit.mento.domain.BoardLike.domain;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.global.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class BoardLike extends BaseEntity {
    private Long boardLikeId;

    private Users user;

    private Board board;

    @Builder.Default
    private Boolean liked = Boolean.FALSE;

    public static BoardLikeEntity from(BoardLike boardLike) {
        return BoardLikeEntity.builder()
                .liked(boardLike.liked)
                .boardEntity(BoardEntity.from(boardLike.getBoard()))
                .user(UsersEntity.from(boardLike.getUser()))
                .build();
    }

    public BoardLike to() {
        return BoardLike.builder()
                .boardLikeId(boardLikeId)
                .board(board)
                .user(user)
                .build();
    }

}
