package com.mentit.mento.domain.chat.controller;

import com.mentit.mento.domain.auth.entity.Users;
import com.mentit.mento.domain.auth.repository.UserRepository;
import com.mentit.mento.domain.chat.service.ChatRoomServiceImpl;
import com.mentit.mento.global.exception.ExceptionCode;
import com.mentit.mento.global.exception.customException.MemberException;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chatroom")
public class ChatRoomController {

    private final ChatRoomServiceImpl chatRoomServiceImpl;
    private final UserRepository userRepository;

    public ChatRoomController(ChatRoomServiceImpl chatRoomServiceImpl, UserRepository userRepository) {
        this.chatRoomServiceImpl = chatRoomServiceImpl;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Void> create(@AuthenticationPrincipal CustomUserDetail user) {

        Users findUserById = userRepository.findById(user.getId()).orElseThrow(
                () -> new MemberException(ExceptionCode.NOT_FOUND_MEMBER)
        );

        chatRoomServiceImpl.createRoom(findUserById, findUserById);

        return ResponseEntity.ok().build();
    }



}
