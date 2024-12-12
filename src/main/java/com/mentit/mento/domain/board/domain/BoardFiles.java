package com.mentit.mento.domain.board.domain;

import com.mentit.mento.global.BaseEntity;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BoardFiles extends BaseEntity {
    private Long boardFileId;

    private String boardFileName;

    private Board boardEntity;

}
