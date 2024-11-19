package com.mentit.mento.domain.board.domain.entity;

import com.mentit.mento.domain.board.domain.BoardFiles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "boardFiles")
public class BoardFilesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardFileId;

    private String boardFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    public static BoardFilesEntity from(BoardFiles createdFile) {
        return BoardFilesEntity.builder()
                .boardFileName(createdFile.getBoardFileName())
                .boardEntity(BoardEntity.from(createdFile.getBoardEntity()))
                .build();
    }

    public BoardFiles to() {
        return BoardFiles.builder()
                .boardFileId(boardFileId)
                .boardEntity(boardEntity.to())
                .build();
    }
}
