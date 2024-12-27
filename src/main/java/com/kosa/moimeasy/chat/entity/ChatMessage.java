package com.kosa.moimeasy.chat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private Long sender;

    @Column(nullable = true, length = 2048) // 길이를 2048로 설정
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Column(name = "file_url", nullable = true, length = 2048)
    private String fileUrl; // 사진/영상 경로

    public enum MessageType {
        TEXT, IMAGE, VIDEO;
    }
}




