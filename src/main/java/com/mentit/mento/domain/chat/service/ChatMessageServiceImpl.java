package com.mentit.mento.domain.chat.service;

import com.mentit.mento.domain.auth.entity.Users;
import com.mentit.mento.domain.auth.repository.UserRepository;
import com.mentit.mento.domain.chat.entity.ChatMessage;
import com.mentit.mento.domain.chat.repository.ChatMessageRepository;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService{

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Override
    public ChatMessage sendMessage(CustomUserDetail user, String text) {

        Users findUserById = userRepository.findById(user.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );

        ChatMessage newMessage = ChatMessage.builder()
                .message("ㅎㅇ")
                .senderId(findUserById.getId())
                .build();

        return chatMessageRepository.save(newMessage);
    }
}
