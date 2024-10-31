package com.mentit.mento.domain.board.entity;

import com.mentit.mento.domain.board.constant.BoardKeywordForCreating;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BoardKeywordForCreatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardKeywordId;

    @Enumerated(EnumType.STRING)
    private BoardKeywordForCreating boardKeyword;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

}
