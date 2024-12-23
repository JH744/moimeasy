package com.kosa.moimeasy.chat.controller;

import com.kosa.moimeasy.chat.dto.CreateRoomDTO;
import com.kosa.moimeasy.chat.dto.SendMessageDTO;
import com.kosa.moimeasy.chat.entity.ChatRoom;
import com.kosa.moimeasy.chat.entity.ChatMessage;
import com.kosa.moimeasy.chat.service.ChatService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<List<CreateRoomDTO>> getAllRooms(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername()); // 사용자 ID 추출
        List<ChatRoom> chatRooms = chatService.getAllRooms(userId);

        // ChatRoom 데이터를 CreateRoomDTO로 변환
        List<CreateRoomDTO> roomDTOs = chatRooms.stream().map(chatRoom -> {
            List<Long> memberIds = chatRoom.getMembers().stream()
                    .map(member -> member.getUser().getUserId())
                    .collect(Collectors.toList());

            // 닉네임 조회
            List<String> memberNicknames = chatService.getMemberNicknames(memberIds);

            return new CreateRoomDTO(
                    chatRoom.getId(),
                    chatRoom.getName(),      // 채팅방 이름
                    chatRoom.getCreatedBy(), // 생성자 ID
                    memberIds,               // 참여자 ID 목록
                    memberNicknames          // 참여자 닉네임 목록
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(roomDTOs); // DTO로 반환
    }




    // 채팅방 생성
    @PostMapping("/room")
    public ResponseEntity<ChatRoom> createRoom(@RequestBody CreateRoomDTO request, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        ChatRoom chatRoom = chatService.createRoom(request, userId);
        return ResponseEntity.ok(chatRoom);
    }


    // 메시지 전송
    @PostMapping("/message")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody SendMessageDTO request, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        request.setSenderId(userId); // 요청의 senderId를 로그인된 사용자 ID로 설정
        ChatMessage message = chatService.sendMessage(request);
        return ResponseEntity.ok(message);
    }

    // 새로운 메시지 폴링
    @GetMapping("/rooms/{roomId}/poll-messages")
    public ResponseEntity<List<Map<String, Object>>> pollMessages(
            @PathVariable Long roomId,
            @RequestParam Long lastMessageId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        // 메시지 조회 및 반환
        List<Map<String, Object>> messages = chatService.getMessagesSince(roomId, lastMessageId);
        return ResponseEntity.ok(messages);
    }

//    @PostMapping("/room/{roomId}/invite")
//    public ResponseEntity<String> inviteMembersToRoom(
//            @PathVariable Long roomId,
//            @RequestBody List<Long> memberIds,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        if (userDetails == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        Long userId = Long.valueOf(userDetails.getUsername());
//        try {
//            chatService.inviteMembersToRoom(roomId, memberIds, userId);
//            return ResponseEntity.ok("회원 초대가 완료되었습니다.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 초대 중 오류 발생: " + e.getMessage());
//        }
//    }








}



