package com.mentit.mento.domain.board.domain.entity;

import com.mentit.mento.domain.board.constant.BoardKeywordForCreatingEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "boardKeywordForCreating")
public class BoardKeywordForCreatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_keyword_id")
    private Long boardKeywordId;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_keyword")
    private BoardKeywordForCreatingEnum boardKeyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

}