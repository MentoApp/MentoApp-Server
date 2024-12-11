package com.mentit.mento.domain.users.infrastructure;

import com.mentit.mento.domain.users.domain.entity.QUsersEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.jpaRepository.UserJPARepository;
import com.mentit.mento.domain.users.service.port.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final UserJPARepository userJPARepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<UsersEntity> findByEmail(String email) {
        log.info("Searching for user in repository with email: {}", email);
        Optional<UsersEntity> userEntity = userJPARepository.findByEmail(email);
        userEntity.ifPresent(entity -> 
            log.info("Found user entity with ID: {}", entity.getUserId())
        );
        return userEntity;
    }

    @Override
    public Optional<UsersEntity> findByNickname(String nickname) {
        log.info("Searching for user in repository with nickname: {}", nickname);
        return userJPARepository.findByNickname(nickname);
    }

    @Override
    public UsersEntity findByBoard(Long userId) {
        log.info("Searching for user in repository with ID: {}", userId);
        return userJPARepository.findByBoardEntities(userId);
    }

    @Override
    public UsersEntity save(UsersEntity usersEntity) {
        return  userJPARepository.save(usersEntity);
    }

    @Override
    public Optional<UsersEntity> findById(Long id) {
        log.info("Searching for user in repository with ID: {}", id);
        return userJPARepository.findById(id);
    }

    @Override
    public void delete(UsersEntity usersEntity) {
        userJPARepository.delete(usersEntity);
    }

    @Override
    public void flush() {
        userJPARepository.flush();
    }

    @Override
    public List<UsersEntity> findUserToDelete(LocalDateTime oneMonthAgo) {
        QUsersEntity users = QUsersEntity.usersEntity;

        return queryFactory.selectFrom(users)
                .where(users.isNewUser.eq(true)
                        .and(users.isDeleted.eq(false))
                        .and(users.createdAt.before(oneMonthAgo)))
                .fetch();
    }

    @Override
    public void deleteAllById(List<Long> userIdsToDelete) {
        userJPARepository.deleteAllById(userIdsToDelete);
    }

    @Override
    public Iterable<UsersEntity> findAll(Sort sort) {
        return userJPARepository.findAll(sort);
    }

    @Override
    public Page<UsersEntity> findAll(Pageable pageable) {
        return userJPARepository.findAll(pageable);
    }
}
