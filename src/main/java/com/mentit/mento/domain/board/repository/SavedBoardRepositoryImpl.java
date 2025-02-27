package com.mentit.mento.domain.board.repository;

import com.mentit.mento.domain.board.domain.entity.BoardEntity;
import com.mentit.mento.domain.board.domain.entity.SavedBoardEntity;
import com.mentit.mento.domain.board.repository.jpaRepository.SavedBoardJPARepository;
import com.mentit.mento.domain.board.service.port.SavedBoardRepository;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class SavedBoardRepositoryImpl implements SavedBoardRepository {
    private final SavedBoardJPARepository savedBoardJPARepository;

    @Override
    public SavedBoardEntity save(SavedBoardEntity savedBoardEntity) {
        return savedBoardJPARepository.save(savedBoardEntity);
    }

    @Override
    public void deleteByBoardAndUser(BoardEntity boardEntity, UsersEntity usersEntity) {
        savedBoardJPARepository.deleteByBoardEntityAndUserEntity(boardEntity, usersEntity);
    }

    @Override
    public List<SavedBoardEntity> findAllByUsers(UsersEntity usersEntity) {
        return savedBoardJPARepository.findAllByUserEntity(usersEntity);
    }

    @Override
    public void flush() {
        savedBoardJPARepository.flush();
    }

    @Override
    public Optional<SavedBoardEntity> findByBoardId(Long boardId) {

        return savedBoardJPARepository.findByBoardEntityBoardId(boardId);
    }


}
