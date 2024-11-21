package com.mentit.mento.domain.board.domain.entity;

import com.mentit.mento.domain.board.constant.BoardKeywordForCreatingEnum;
import com.mentit.mento.domain.board.domain.BoardKeywordForCreating;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "boardKeywordForCreating")
public class BoardKeywordForCreatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardKeywordId;

    @Enumerated(EnumType.STRING)
    private BoardKeywordForCreatingEnum boardKeyword;

    @ManyToOne(fetch = FetchType.LAZY)
    private BoardEntity boardEntity;

    public BoardKeywordForCreating to() {
        return BoardKeywordForCreating.builder()
                .boardKeywordId(boardKeywordId)
                .boardKeyword(boardKeyword)
                .boardEntity(boardEntity.to())
                .build();
    }
}
