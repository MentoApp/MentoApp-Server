package com.mentit.mento.domain.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Document(collection = "chat_messages")
@Builder(toBuilder = true)
@AllArgsConstructor
public class ChatMessage {

    @Id
    private String id;

    private Long roomId;
    private Long senderId;
    private String message;
    private LocalDateTime sentAt;


}
