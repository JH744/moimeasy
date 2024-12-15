package com.kosa.moimeasy.chat.repository;

import com.kosa.moimeasy.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdAndIdGreaterThanOrderByCreatedAtAsc(Long chatRoomId, Long id);
}

