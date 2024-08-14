package com.mentit.mento.domain.chat.service;

import com.mentit.mento.domain.chat.entity.ChatMessage;
import com.mentit.mento.global.security.userDetails.CustomUserDetail;

public interface ChatMessageService {

    ChatMessage sendMessage(CustomUserDetail user, String text);
}
