package com.mentit.mento.domain.users.service.port;

import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<UsersEntity, Long> {

    Optional<UsersEntity> findByEmail(String email);

    Optional<UsersEntity> findByNickname(String nickname);

    UsersEntity findByBoard(Long userId);

    UsersEntity save(UsersEntity usersEntity);

    Optional<UsersEntity> findById(Long id);

    void delete(UsersEntity usersEntity);

    void flush();

    List<UsersEntity> findUserToDelete(LocalDateTime oneMonthAgo);

    void deleteAllById(List<Long> userIdsToDelete);

    @Override
    Iterable<UsersEntity> findAll(Sort sort);

    @Override
    Page<UsersEntity> findAll(Pageable pageable);
}
