package com.kosa.moimeasy.chat.service;

import com.kosa.moimeasy.chat.entity.ChatRoom;
import com.kosa.moimeasy.chat.dto.CreateRoomDTO;
import com.kosa.moimeasy.chat.dto.SendMessageDTO;
import com.kosa.moimeasy.chat.entity.ChatMessage;
import com.kosa.moimeasy.chat.entity.ChatMessage.MessageType;
import com.kosa.moimeasy.chat.entity.ChatRoomUser;
import com.kosa.moimeasy.chat.repository.ChatRoomRepository;
import com.kosa.moimeasy.chat.repository.ChatRoomUserRepository;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import com.kosa.moimeasy.chat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    @Transactional
    public ChatRoom createRoom(CreateRoomDTO request, Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long userMoeimId = currentUser.getMoeimId();

        // `moeimId`를 기반으로 멤버 필터링
        List<User> members = userRepository.findAllById(request.getMemberIds())
                .stream()
                .filter(user -> user.getMoeimId().equals(userMoeimId))
                .collect(Collectors.toList());

        if (members.isEmpty()) {
            throw new IllegalArgumentException("모임 회원이 아닙니다.");
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(request.getRoomName() != null ? request.getRoomName() : generateDefaultRoomName(members));
        chatRoom.setCreatedBy(userId);

        chatRoom = chatRoomRepository.save(chatRoom);

        // ChatRoomUser 생성 및 저장
        ChatRoom finalChatRoom = chatRoom;
        members.forEach(user -> {
            ChatRoomUser chatRoomUser = new ChatRoomUser();
            chatRoomUser.setChatRoom(finalChatRoom); // finalChatRoom 사용
            chatRoomUser.setUser(user);
            chatRoomUser.setUserNickname(user.getNickname());
            chatRoomUserRepository.save(chatRoomUser);
        });

        return chatRoom;
    }




    private String generateDefaultRoomName(List<User> members) {
        return members.stream()
                .map(User::getNickname)
                .limit(3)
                .collect(Collectors.joining(", ")) +
                (members.size() > 3 ? " 외 " + (members.size() - 3) + "명" : "");
    }


    @Transactional
    public ChatMessage sendMessage(SendMessageDTO request) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 사용자가 채팅방에 포함되어 있는지 확인
        boolean isMember = chatRoom.getMembers().stream()
                .anyMatch(member -> member.getUser().getUserId().equals(request.getSenderId()));

        if (!isMember) {
            throw new IllegalArgumentException("사용자는 이 채팅방의 멤버가 아닙니다.");
        }

        ChatMessage message = new ChatMessage();
        message.setSender(request.getSenderId());
        message.setChatRoom(chatRoom);
        message.setMessageType(MessageType.valueOf(request.getMessageType().toUpperCase()));

        if (request.getMessageType().equalsIgnoreCase("TEXT")) {
            message.setContent(request.getContent());
        } else {
            message.setFileUrl(request.getFileUrl()); // 파일 경로 저장
        }

        return chatMessageRepository.save(message);
    }


    public List<ChatRoom> getAllRooms(Long userId) {
        return chatRoomRepository.findByUserId(userId); // 적절한 Repository 메서드 호출
    }


    public List<ChatMessage> getMessagesSince(Long roomId, Long lastMessageId) {
        return chatMessageRepository.findByChatRoomIdAndIdGreaterThanOrderByCreatedAtAsc(roomId, lastMessageId);
    }



}




