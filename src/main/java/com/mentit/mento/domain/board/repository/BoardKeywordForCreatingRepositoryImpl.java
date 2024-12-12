package com.mentit.mento.domain.board.repository;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.domain.entity.BoardKeywordForCreatingEntity;
import com.mentit.mento.domain.board.repository.jpaRepository.BoardKeywordForCreatingJPARepository;
import com.mentit.mento.domain.board.service.port.BoardKeywordForCreatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BoardKeywordForCreatingRepositoryImpl implements BoardKeywordForCreatingRepository {
    private final BoardKeywordForCreatingJPARepository boardKeywordForCreatingJPARepository;

    @Override
    public BoardKeywordForCreatingEntity save(BoardKeywordForCreatingEntity boardKeywordForCreatingEntity) {
        return boardKeywordForCreatingJPARepository.save(boardKeywordForCreatingEntity);
    }

    @Override
    public void deleteAllByBoard(BoardEntity boardEntity) {
        boardKeywordForCreatingJPARepository.deleteAllByBoardEntity(boardEntity);
    }
}
