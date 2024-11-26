package com.mentit.mento.domain.board.service.port;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    BoardEntity save(BoardEntity createdBoard);

    Optional<BoardEntity> findByBoardId(Long boardId);

    void delete(BoardEntity findBoard);

    Optional<List<BoardEntity>> findTop3ByOrderByViewCountDesc();

    List<BoardEntity> findAll();

    Page<BoardEntity> findByUsers(UsersEntity usersEntity, Pageable pageable);

    List<BoardEntity> findAllByUsers(Long usersId);
}
