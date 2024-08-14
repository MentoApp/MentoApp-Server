package com.mentit.mento.domain.chat.repository;

import com.mentit.mento.domain.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableMongoRepositories
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
}
