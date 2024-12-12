package com.mentit.mento.domain.board.domain.entity;

import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "savedBoard")
public class SavedBoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "saved_board_id")
    private Long savedBoardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private UsersEntity userEntity;
}