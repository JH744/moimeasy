package com.kosa.moimeasy.chat.controller;

import com.kosa.moimeasy.chat.dto.CreateRoomDTO;
import com.kosa.moimeasy.chat.dto.SendMessageDTO;
import com.kosa.moimeasy.chat.entity.ChatRoom;
import com.kosa.moimeasy.chat.entity.ChatMessage;
import com.kosa.moimeasy.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "채팅페이지", description = "Chat API")
@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 채팅방 목록 조회
    @Operation(summary = "채팅방 조회", description = "회원이 속한 채팅방을 보여줄 때 사용하는 API")
    @ApiResponse(responseCode = "200", description = "채팅방 조회 성공")
    @ApiResponse(responseCode = "401", description = "로그인하지 않은 사용자")
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
    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "채팅방 생성 성공")
    @ApiResponse(responseCode = "401", description = "로그인하지 않은 사용자")
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
    @Operation(summary = "메세지 전송", description = "메세지를 전송합니다.")
    @ApiResponse(responseCode = "200", description = "메세지 전송 성공")
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
    @Operation(summary = "메세지 폴링", description = "메세지를 받아옵니다.")
    @ApiResponse(responseCode = "200", description = "메세지 폴링 성공")
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

    // 회원 초대
    @Operation(summary = "채팅방 초대", description = "채팅방에 초대합니다.")
    @ApiResponse(responseCode = "200", description = "채팅방 초대 성공")
    @PostMapping("/room/{roomId}/invite")
    public ResponseEntity<String> inviteMembersToRoom(
            @PathVariable Long roomId,
            @RequestBody List<Long> memberIds,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        try {
            chatService.inviteMembersToRoom(roomId, memberIds, userId);
            return ResponseEntity.ok("회원 초대가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 초대 중 오류 발생: " + e.getMessage());
        }
    }

    // 채팅방 나가기
    @Operation(summary = "채팅방 나가기", description = "채팅방에서 탈주합니다.")
    @ApiResponse(responseCode = "200", description = "채팅방 탈출 성공")
    @DeleteMapping("/room/{roomId}/leave")
    public ResponseEntity<String> leaveRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = Long.valueOf(userDetails.getUsername());
        try {
            chatService.leaveRoom(roomId, userId);
            return ResponseEntity.ok("채팅방을 성공적으로 나갔습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("채팅방 나가기 중 오류 발생: " + e.getMessage());
        }
    }








}



