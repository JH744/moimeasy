package com.kosa.moimeasy.chat.controller;

import com.kosa.moimeasy.chat.dto.CreateRoomDTO;
import com.kosa.moimeasy.chat.dto.SendMessageDTO;
import com.kosa.moimeasy.chat.entity.ChatRoom;
import com.kosa.moimeasy.chat.entity.ChatMessage;
import com.kosa.moimeasy.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getAllRooms(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername()); // 사용자 ID 추출
        List<ChatRoom> chatRooms = chatService.getAllRooms(userId);

//        // 필요한 데이터만 포함하도록 members 필터링
//        chatRooms.forEach(chatRoom -> chatRoom.getMembers().forEach(member -> {
//            member.setChatRoom(null); // 순환 참조 방지
//        }));


        // ChatRoom 데이터를 CreateRoomDTO로 변환
        List<CreateRoomDTO> roomDTOs = chatRooms.stream().map(chatRoom -> {
            List<Long> memberIds = chatRoom.getMembers().stream()
                    .map(member -> member.getUser().getUserId())
                    .collect(Collectors.toList());

            // 닉네임 조회
            List<String> memberNicknames = chatService.getMemberNicknames(memberIds);

            return new CreateRoomDTO(
                    chatRoom.getName(), // 채팅방 이름
                    chatRoom.getCreatedBy(), // 생성자 ID
                    memberIds, // 참여자 ID 목록
                    memberNicknames // 참여자 닉네임 목록
            );
        }).collect(Collectors.toList());


        return ResponseEntity.ok(chatRooms);
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
    public ResponseEntity<List<ChatMessage>> pollMessages(
            @PathVariable Long roomId,
            @RequestParam Long lastMessageId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        // Optional: 사용자가 해당 방의 멤버인지 검증 로직 추가 가능
        List<ChatMessage> messages = chatService.getMessagesSince(roomId, lastMessageId);
        return ResponseEntity.ok(messages);
    }





//     @PostMapping("/upload")
// public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
//     try {
//         String filePath = fileService.uploadFile(file);
//         return ResponseEntity.ok(filePath);
//     } catch (Exception e) {
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
//     }
// }
}



