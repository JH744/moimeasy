package com.kosa.moimeasy.chat.service;

import com.kosa.moimeasy.chat.entity.ChatRoom;
import com.kosa.moimeasy.chat.dto.CreateRoomDTO;
import com.kosa.moimeasy.chat.dto.SendMessageDTO;
import com.kosa.moimeasy.chat.entity.ChatMessage;

import com.kosa.moimeasy.chat.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

import com.kosa.moimeasy.chat.repository.ChatMessageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatRoom createRoom(CreateRoomDTO request) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(request.getRoomName());
        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public ChatMessage sendMessage(SendMessageDTO request) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        ChatMessage message = new ChatMessage();
        message.setContent(request.getContent());
        message.setSender(request.getSenderId());
        message.setChatRoom(chatRoom);
        return chatMessageRepository.save(message);
    }

    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }
}



