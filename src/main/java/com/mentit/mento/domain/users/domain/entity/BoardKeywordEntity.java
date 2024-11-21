package com.mentit.mento.domain.users.domain.entity;

import com.mentit.mento.domain.users.constant.BoardKeywordEnum;
import com.mentit.mento.domain.users.domain.BoardKeyword;
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
@Table(name = "boardKeyword")
public class BoardKeywordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardKeywordId;

    @Enumerated(EnumType.STRING)
    private BoardKeywordEnum boardKeywordEnum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UsersEntity usersEntity;


    public BoardKeyword toModel() {
        return BoardKeyword.builder()
                .boardKeywordId(this.boardKeywordId)
                .boardKeyword(this.boardKeywordEnum)
                .usersEntity(this.usersEntity)
                .build();
    }
}
