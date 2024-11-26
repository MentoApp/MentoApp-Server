package com.mentit.mento.domain.board.repository.jpaRepository;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardEntityJPARepository extends JpaRepository<BoardEntity,Long> {
    Optional<BoardEntity> findByBoardId(Long boardId);

    Optional<List<BoardEntity>> findTop3ByOrderByViewCountDesc();

    Page<BoardEntity> findAllByWriter(UsersEntity usersEntity, Pageable pageable);

    @Query("select b from BoardEntity b where b.writer.userId = :usersId")
    List<BoardEntity> findAllByUsers(Long usersId);
}
