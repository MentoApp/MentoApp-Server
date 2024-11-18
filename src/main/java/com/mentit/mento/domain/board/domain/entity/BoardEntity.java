package com.mentit.mento.domain.board.domain.entity;

import com.mentit.mento.domain.board.constant.BoardTypeEnum;
import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.comment.entity.Comment;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetails;
import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = " update board set is_deleted = true where board_id = ?")
public class BoardEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardTypeEnum boardTypeEnum;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private UsersEntity writer;

    @Column(nullable = false)
    private String content;

    @Column
    private Long viewCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dotori_token_usage_detail_id")
    private DotoriTokenUsageDetails dotoriTokenUsageDetail;

    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<BoardKeywordForCreatingEntity> boardKeywordForCreatings = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = Boolean.FALSE;

    @OneToMany(mappedBy = "board",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<BoardFilesEntity> boardFileEntities = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();


    public static BoardEntity from(Board createdBoard) {
        return BoardEntity.builder()
                .boardTypeEnum(createdBoard.getBoardTypeEnum())
                .title(createdBoard.getTitle())
                .writer(createdBoard.getWriter())
                .content(createdBoard.getContent())
                .viewCount(createdBoard.getViewCount())
                .isDeleted(createdBoard.isDeleted())
                .dotoriTokenUsageDetail(createdBoard.getDotoriTokenUsageDetail())
                .boardKeywordForCreatings(createdBoard.getBoardKeywordForCreatings().stream().map(BoardKeywordForCreatingEntity::from).toList())
                .boardFileEntities(createdBoard.getBoardFileEntities().stream().map(BoardFilesEntity::from).toList())
                .comments(createdBoard.getComments())
                .build();
    }

    public Board to() {
        return Board.builder()
                .boardId(boardId)
                .boardTypeEnum(boardTypeEnum)
                .title(title)
                .writer(writer)
                .content(content)
                .viewCount(viewCount)
                .isDeleted(isDeleted)
                .dotoriTokenUsageDetail(dotoriTokenUsageDetail)
                .boardKeywordForCreatings(boardKeywordForCreatings.stream().map(BoardKeywordForCreatingEntity::to).toList())
                .boardFileEntities(boardFileEntities.stream().map(BoardFilesEntity::to).toList())
                .comments(comments)
                .build();
    }
}
