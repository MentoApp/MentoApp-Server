package com.mentit.mento.domain.board.domain.entity;

import com.mentit.mento.domain.board.constant.BoardTypeEnum;
import com.mentit.mento.domain.comment.entity.CommentEntity;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetailsEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "board")
@SQLDelete(sql = " update board set is_deleted = true where board_id = ?")
@SQLRestriction("is_deleted = false")
public class BoardEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
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

    @OneToMany(mappedBy = "boardEntity")
    private List<DotoriTokenUsageDetailsEntity> dotoriTokenUsageDetail;

    @OneToMany(mappedBy = "boardEntity", fetch = FetchType.EAGER)
    @Builder.Default
    private List<BoardKeywordForCreatingEntity> boardKeywordForCreatings = new ArrayList<>();

    @Column(name="is_deleted",nullable = false)
    @Builder.Default
    private boolean isDeleted = Boolean.FALSE;

    @OneToMany(mappedBy = "boardEntity",fetch = FetchType.EAGER)
    @Builder.Default
    private List<BoardFilesEntity> boardFileEntities = new ArrayList<>();

    @OneToMany(mappedBy = "boardEntity")
    @Builder.Default
    private List<CommentEntity> commentEntities = new ArrayList<>();


}
