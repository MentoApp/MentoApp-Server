package com.mentit.mento.domain.chat.controller;

import com.mentit.mento.domain.chat.service.ChatMessageServiceImpl;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatMessageController {

    private final ChatMessageServiceImpl chatMessageServiceImpl;

    public ChatMessageController(ChatMessageServiceImpl chatMessageServiceImpl) {
        this.chatMessageServiceImpl = chatMessageServiceImpl;
    }

    @PostMapping
    public ResponseEntity<String> sendMessage(
            @AuthenticationPrincipal CustomUserDetail user,
            @RequestBody String message
            ) {

        chatMessageServiceImpl.sendMessage(user, message);

        return ResponseEntity.ok().body("저장됨");
    }
}
