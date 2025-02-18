package com.mentit.mento.global.helper;

import com.mentit.mento.domain.users.domain.entity.UsersEntity;
import com.mentit.mento.domain.users.service.port.UserRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserHelper {
    private final UserRepository userRepository;

    public UsersEntity getUsers(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );
    }
}
