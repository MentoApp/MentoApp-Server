package com.mentit.mento.domain.users.infrastructure;

import com.mentit.mento.domain.users.domain.UserStatusTag;
import com.mentit.mento.domain.users.domain.Users;
import com.mentit.mento.domain.users.domain.entity.UserStatusTagEntity;
import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.infrastructure.jpaRepository.UserStatusTagJPARepository;
import com.mentit.mento.domain.users.service.port.UserStatusTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserStatusTagRepositoryImpl implements UserStatusTagRepository {

    private final UserStatusTagJPARepository userStatusTagJPARepository;


    @Override
    public void deleteAllByUsers(Users findUserByUserDetail) {

    }

    @Override
    public Optional<UserStatusTag> findByUsers(Users user) {
        return userStatusTagJPARepository.findByUsers(UsersEntity.from(user));
    }

    @Override
    public UserStatusTag save(UserStatusTag userStatusTag) {
        return userStatusTagJPARepository.save(UserStatusTagEntity.from(userStatusTag)).to();
    }

    @Override
    public void delete(UserStatusTag findUserStatusTag) {
        userStatusTagJPARepository.delete(UserStatusTagEntity.from(findUserStatusTag));
    }
}
