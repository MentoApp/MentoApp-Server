package com.mentit.mento.domain.board.repository;

import com.mentit.mento.domain.board.domain.Board;
import com.mentit.mento.domain.board.domain.BoardKeywordForCreating;
import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.domain.entity.BoardKeywordForCreatingEntity;
import com.mentit.mento.domain.board.repository.jpaRepository.BoardKeywordForCreatingJPARepository;
import com.mentit.mento.domain.board.service.port.BoardKeywordForCreatingRepository;
import com.mentit.mento.domain.users.domain.entity.BoardKeywordEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BoardKeywordForCreatingRepositoryImpl implements BoardKeywordForCreatingRepository {
    private final BoardKeywordForCreatingJPARepository boardKeywordForCreatingJPARepository;

    @Override
    public BoardKeywordForCreating save(BoardKeywordForCreating savedBoardKeyword) {
        return boardKeywordForCreatingJPARepository.save(BoardKeywordForCreatingEntity.from(savedBoardKeyword)).to();
    }

    @Override
    public void deleteAllByBoard(Board findBoard) {
        boardKeywordForCreatingJPARepository.deleteAllByBoard(BoardEntity.from(findBoard));
    }
}
