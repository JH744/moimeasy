package com.kosa.moimeasy.chat.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomDTO {
    private String roomName; // 사용자가 설정한 채팅방 이름 (선택사항)
    private Long createdBy; // 채팅방 생성자 ID
    private List<Long> memberIds; // 채팅에 참여하는 사용자 ID 목록
}