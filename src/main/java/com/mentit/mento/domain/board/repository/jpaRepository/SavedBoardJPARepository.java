package com.mentit.mento.domain.board.repository.jpaRepository;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.domain.entity.SavedBoardEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedBoardJPARepository extends JpaRepository<SavedBoardEntity,Long> {
    void deleteByBoardEntityAndUserEntity(BoardEntity boardEntity, UsersEntity usersEntity);

    List<SavedBoardEntity> findAllByUserEntity(UsersEntity usersEntity);

    Optional<SavedBoardEntity> findByBoardEntityBoardId(Long boardId);
}
