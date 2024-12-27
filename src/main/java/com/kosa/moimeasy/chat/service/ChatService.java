package com.kosa.moimeasy.chat.service;

import com.kosa.moimeasy.chat.entity.ChatRoom;
import com.kosa.moimeasy.chat.dto.CreateRoomDTO;
import com.kosa.moimeasy.chat.dto.SendMessageDTO;
import com.kosa.moimeasy.chat.entity.ChatMessage;
import com.kosa.moimeasy.chat.entity.ChatMessage.MessageType;
import com.kosa.moimeasy.chat.entity.ChatRoomUser;
import com.kosa.moimeasy.chat.repository.ChatRoomRepository;
import com.kosa.moimeasy.chat.repository.ChatRoomUserRepository;
import com.kosa.moimeasy.moeim.entity.Moeim;
import com.kosa.moimeasy.moeim.repository.MoeimRepository;
import com.kosa.moimeasy.user.entity.User;
import com.kosa.moimeasy.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import com.kosa.moimeasy.chat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final MoeimRepository moeimRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    @Transactional
    public ChatRoom createRoom(CreateRoomDTO request, Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Long userMoeimId = currentUser.getMoeimId();

        // 요청된 멤버 ID 리스트에 현재 로그인된 사용자 ID를 추가
        if (!request.getMemberIds().contains(userId)) {
            request.getMemberIds().add(userId);
        }

        // `moeimId`를 기반으로 멤버 필터링
        List<User> members = userRepository.findAllById(request.getMemberIds())
                .stream()
                .filter(user -> user.getMoeimId().equals(userMoeimId)) // 같은 모임에 속한 회원만 필터링
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
            chatRoomUser.setChatRoom(finalChatRoom);
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
        } else if (request.getMessageType().equalsIgnoreCase("IMAGE") || request.getMessageType().equalsIgnoreCase("VIDEO")) {
            if (request.getFileUrl() == null || request.getFileUrl().isEmpty()) {
                throw new IllegalArgumentException("파일 URL이 제공되지 않았습니다.");
            }
            message.setContent(request.getFileUrl()); // URL 저장
            message.setFileUrl(request.getFileUrl());
        }
        else {
            throw new IllegalArgumentException("지원되지 않는 메시지 타입입니다.");
        }

        return chatMessageRepository.save(message);
    }


    public List<ChatRoom> getAllRooms(Long userId) {
        return chatRoomRepository.findByUserId(userId); // 적절한 Repository 메서드 호출
    }


    public List<Map<String, Object>> getMessagesSince(Long roomId, Long lastMessageId) {
        List<ChatMessage> messages = chatMessageRepository
                .findByChatRoomIdAndIdGreaterThanOrderByCreatedAtAsc(roomId, lastMessageId);

        return messages.stream().map(message -> {
            // senderId로 닉네임 조회
            String senderNickname = userRepository.findById(message.getSender())
                    .map(User::getNickname)
                    .orElse("알 수 없는 사용자");

            // 결과를 Map으로 반환
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("id", message.getId());
            messageData.put("content", message.getContent());
            messageData.put("senderId", message.getSender());
            messageData.put("senderNickname", senderNickname);
            messageData.put("messageType", message.getMessageType().toString());
            messageData.put("timestamp", message.getCreatedAt());
            return messageData;
        }).collect(Collectors.toList());
    }

    public String getUserNicknameById(Long userId) {
        return userRepository.findById(userId)
                .map(User::getNickname)
                .orElse("알 수 없는 사용자");
    }



    @Transactional
    public List<String> getMemberNicknames(List<Long> memberIds) {
        // UserRepository를 통해 사용자 닉네임 조회
        return userRepository.findAllById(memberIds).stream()
                .map(User::getNickname)
                .collect(Collectors.toList());
    }


    @Transactional
    public void inviteMembersToRoom(Long roomId, List<Long> memberIds, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 단체톡방은 초대 불가
        if (chatRoom.getMoeim() != null) {
            throw new IllegalArgumentException("단체톡방에서는 초대가 불가능합니다.");
        }

        List<User> usersToInvite = userRepository.findAllById(memberIds);
        for (User user : usersToInvite) {
            boolean alreadyMember = chatRoom.getMembers().stream()
                    .anyMatch(member -> member.getUser().getUserId().equals(user.getUserId()));

            if (!alreadyMember) {
                ChatRoomUser chatRoomUser = new ChatRoomUser();
                chatRoomUser.setChatRoom(chatRoom);
                chatRoomUser.setUser(user);
                chatRoomUser.setUserNickname(user.getNickname());
                chatRoomUserRepository.save(chatRoomUser);
            }
        }
    }

    @Transactional
    public void leaveRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 멤버를 찾을 수 없습니다."));

        chatRoomUserRepository.delete(chatRoomUser);

        // 멤버가 남아있지 않으면 채팅방 삭제
        if (chatRoomUserRepository.countByChatRoomId(roomId) == 0) {
            chatRoomRepository.delete(chatRoom);
        }
    }





}




