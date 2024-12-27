package com.kosa.moimeasy.chat.entity;

import com.kosa.moimeasy.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_room_users")
@Getter
@Setter
public class ChatRoomUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_nickname", nullable = false)
    private String userNickname; // 캐싱된 닉네임(필요 시)

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;
}



