package com.mentit.mento.domain.board.repository;

import com.mentit.mento.domain.board.entity.Board;
import com.mentit.mento.domain.board.entity.BoardFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardFileRepository extends JpaRepository<BoardFiles,Long> {
    void deleteAllByBoard(Board findBoard);

    List<BoardFiles> findAllByBoard(Board findBoard);
}
