package com.kosa.moimeasy.chat.dto;

import com.kosa.moimeasy.chat.entity.ChatMessage.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageDTO {
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private String messageType; // TEXT, IMAGE, VIDEO
    private String fileUrl; // 파일 경로
}
