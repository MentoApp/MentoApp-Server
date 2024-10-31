package com.mentit.mento.domain.board.entity;

import com.mentit.mento.domain.board.constant.BoardType;
import com.mentit.mento.domain.comment.entity.Comment;
import com.mentit.mento.domain.dotoriToken.entity.DotoriTokenUsageDetails;
import com.mentit.mento.domain.users.entity.Users;
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
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private Users writer;

    @Column(nullable = false)
    private String content;

    @Column
    private Long viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
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
    private List<BoardFiles> boardFiles = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();


}
