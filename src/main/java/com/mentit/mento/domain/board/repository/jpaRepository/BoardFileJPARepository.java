package com.mentit.mento.domain.board.repository.jpaRepository;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.domain.entity.BoardFilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardFileJPARepository extends JpaRepository<BoardFilesEntity,Long> {
    void deleteAllByBoard(BoardEntity findBoardEntity);

    List<BoardFilesEntity> findAllByBoard(BoardEntity findBoardEntity);
}
