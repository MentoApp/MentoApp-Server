package com.mentit.mento.domain.BoardLike.repository;

import com.mentit.mento.domain.BoardLike.domain.BoardLikeEntity;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardLikeJPARepository extends JpaRepository<BoardLikeEntity,Long> {

    Optional<BoardLikeEntity> findBoardLikeByBoardAndUser(BoardEntity boardEntity, UsersEntity user);

    long countByBoard(BoardEntity boardEntity);
}
