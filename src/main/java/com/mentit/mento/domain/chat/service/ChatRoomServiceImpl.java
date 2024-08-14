package com.mentit.mento.domain.chat.service;

import com.mentit.mento.domain.auth.entity.Users;
import com.mentit.mento.domain.chat.entity.ChatRoom;
import com.mentit.mento.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public ChatRoom createRoom(Users mentor, Users mentee) {

        ChatRoom chatRoom = ChatRoom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .status(ChatRoom.ChatRoomStatus.ACTIVE)
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public List<ChatRoom> findAllRoom() {
        return List.of();
    }

    @Override
    public ChatRoom findRoomById() {
        return null;
    }

    @Override
    public void deleteRoomById() {

    }
}
