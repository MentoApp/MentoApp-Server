package com.mentit.mento.domain.chat.service;


import com.mentit.mento.domain.auth.entity.Users;
import com.mentit.mento.domain.chat.entity.ChatRoom;

import java.util.List;

public interface ChatRoomService {
    ChatRoom createRoom(Users mentor, Users mentee);
    List<ChatRoom> findAllRoom();
    ChatRoom findRoomById();
    void deleteRoomById();
}
